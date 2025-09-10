package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

@Component
@Scope("prototype")
@Slf4j
public class RequestQueue {
    private final ContextRegistry contextRegistry;
    private final Sinks.Many<Request<?>> sink = Sinks.many().replay().all();
    private final Sinks.One<Integer> shutDownSink = Sinks.one();

    private final Map<Class<?>, Function<?, Object>> subjectKeysByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Actions<?>> actionsByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, ContextEntries<?>> contextEntriesByRequestType = new ConcurrentHashMap<>();

    @Getter
    private final Flux<Void> flux;

    public RequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistry contextRegistry) {
        this.contextRegistry = contextRegistry;
        this.flux = sink.asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnError(_e -> fatalStartupErrorHandler.shutdown())
                .takeUntilOther(shutDownSink.asMono())
                .groupBy(it -> subjectKeyHash(it) % 100)
                .flatMap(f -> f
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(this::processRequest));
        Hooks.enableAutomaticContextPropagation();
    }

    public <T> void queueRequest(Request<T> request) {
        sink.emitNext(request, this::emitFailureHandler);
    }

    public <T> void on(Class<T> requestType, Function<T, Mono<Void>> action, RequestOrderSpec<T> requestOrderSpec, Collection<ContextEntrySpec<T>> contextEntries) {
        onWrapped(requestType, wrapped(action), wrapped(requestOrderSpec), wrapped(contextEntries));
    }

    @SuppressWarnings("unchecked")
    public <T> void onWrapped(Class<T> requestType, Function<Request<T>, Mono<Void>> action, RequestOrderSpec<Request<T>> requestOrderSpec, Collection<ContextEntrySpec<Request<T>>> contextEntries) {
        registerContextEntries(contextEntries);
        ((Actions<T>) actionsByRequestType.computeIfAbsent(requestType, _k -> Actions.empty())).add(action);
        subjectKeysByRequestType.put(requestType, requestOrderSpec.keyExtractor());
        contextEntriesByRequestType.put(requestType, new ContextEntries<>(contextEntries));
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<Void> processRequest(Request<T> request) {
        var requestType = request.body().getClass();
        var actions = (Actions<T>) actionsByRequestType.getOrDefault(requestType, Actions.empty());
        var contextEntries = (ContextEntries<Request<T>>) contextEntriesByRequestType.getOrDefault(requestType, new ContextEntries<>(List.of()));

        return Flux.just(request)
                .flatMap(actions::apply)
                .doOnError(e -> log.error(e.getMessage()))
                .contextWrite(requestContext(request, contextEntries))
                .then();
    }

    private <T> Context requestContext(Request<T> request, ContextEntries<Request<T>> contextEntries) {
        return Context.of(
                contextEntries.contextEntries().stream().collect(Collectors.toMap(
                        ContextEntrySpec::key,
                        entry -> entry.valueExtractor().apply(request)
                )));
    }

    private <T> int subjectKeyHash(Request<T> request) {
        @SuppressWarnings("unchecked")
        var subjectKeyHashFunction = (Function<Request<T>, Object>) subjectKeysByRequestType.getOrDefault(request.body().getClass(), Object::hashCode);
        return Optional.ofNullable(subjectKeyHashFunction.apply(request))
                .map(Object::hashCode)
                .orElse(0); // guarantee order of all if no subject key is provided
    }

    // gotta declare this wrapper, otherwise cannot put Collection<ContextEntries<T>> to Map<Class<?>, Collection<ContextEntries<?>>>
    private record ContextEntries<T>(
            Collection<ContextEntrySpec<T>> contextEntries
    ) {
        public static <T> ContextEntries<T> of(Collection<ContextEntrySpec<T>> contextEntries) {
            return new ContextEntries<>(contextEntries);
        }
    }

    private record Actions<T>(
            List<Function<Request<T>, Mono<Void>>> actions
    ) {
        public static <T> Actions<T> of(Collection<Function<Request<T>, Mono<Void>>> actions) {
            return new Actions<>(new CopyOnWriteArrayList<>(actions));
        }

        public static <T> Actions<T> empty() {
            return of(List.of());
        }

        public void add(Function<Request<T>, Mono<Void>> action) {
            actions.add(action);
        }

        public Flux<Void> apply(Request<T> request) {
            return Flux.fromIterable(actions)
                    .flatMap(action -> action.apply(request));
        }
    }

    private <T> void registerContextEntries(Collection<ContextEntrySpec<T>> contextEntries) {
        contextEntries.forEach(contextEntry ->
                contextRegistry.registerThreadLocalAccessor(
                        contextEntry.key(),
                        () -> MDC.get(contextEntry.key()),
                        v -> MDC.put(contextEntry.key(), v),
                        () -> MDC.remove(contextEntry.key())));
    }

    private boolean emitFailureHandler(SignalType signalType, Sinks.EmitResult emitResult) {
        if (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
            LockSupport.parkNanos(10);
            return true;
        }

        if (emitResult.isFailure()) {
            log.error("Failed to handle request: {}", emitResult.name());
        }

        return false;
    }

    @PreDestroy
    public void shutdown() {
        if (!shutDownSink.tryEmitValue(0).isSuccess()) {
            log.error("Failed to emit shutdown signal for RequestQueue");
        }
        sink.tryEmitComplete();
        log.info("RequestQueue has been shut down successfully");
    }
}
