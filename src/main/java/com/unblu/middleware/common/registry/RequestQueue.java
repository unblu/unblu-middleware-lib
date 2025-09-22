package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.entity.ContextEntries;
import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import static com.unblu.middleware.common.registry.ContextRegistryWrapper.requestContext;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

@Component
@Scope("prototype")
@Slf4j
public class RequestQueue {
    private final ContextRegistryWrapper contextRegistryWrapper;
    private final Sinks.Many<Request<?>> sink = Sinks.many().replay().all();
    private final Sinks.One<Integer> shutDownSink = Sinks.one();

    private final Map<Class<?>, Function<?, Object>> subjectKeysByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Actions<?>> actionsByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, ContextEntries<?>> contextEntriesByRequestType = new ConcurrentHashMap<>();

    @Getter
    private final Flux<Void> flux;

    public RequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper) {
        this.contextRegistryWrapper = contextRegistryWrapper;
        this.flux = sink.asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnError(_e -> fatalStartupErrorHandler.shutdown())
                .takeUntilOther(shutDownSink.asMono())
                .groupBy(it -> subjectKeyHash(it) % 100) // max 100 parallel
                .flatMap(f -> f
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(this::processRequest));
    }

    public <T> void queueRequest(Request<T> request) {
        sink.emitNext(request, this::emitFailureHandler);
    }

    public <T> void on(Class<T> requestType, Function<T, Mono<Void>> action, RequestOrderSpec<T> requestOrderSpec, Collection<ContextEntrySpec<T>> contextEntries) {
        onWrapped(requestType, wrapped(action), wrapped(requestOrderSpec), wrapped(contextEntries));
    }

    @SuppressWarnings("unchecked")
    public <T> void onWrapped(Class<T> requestType, Function<Request<T>, Mono<Void>> action, RequestOrderSpec<Request<T>> requestOrderSpec, Collection<ContextEntrySpec<Request<T>>> contextEntries) {
        contextRegistryWrapper.registerContextEntries(contextEntries);
        ((Actions<T>) actionsByRequestType.computeIfAbsent(requestType, _k -> Actions.empty())).add(action);
        subjectKeysByRequestType.put(requestType, requestOrderSpec.keyExtractor());
        contextEntriesByRequestType.put(requestType, new ContextEntries<>(contextEntries));
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<Void> processRequest(Request<T> request) {
        var requestType = request.body().getClass();
        var actions = (Actions<T>) actionsByRequestType.getOrDefault(requestType, Actions.empty());
        var contextEntries = (ContextEntries<Request<T>>) contextEntriesByRequestType.getOrDefault(requestType, ContextEntries.empty());

        return Flux.just(request)
                .flatMap(actions::apply)
                .doOnError(e -> log.error(e.getMessage()))
                .contextWrite(requestContext(request, contextEntries))
                .then();
    }

    private <T> int subjectKeyHash(Request<T> request) {
        @SuppressWarnings("unchecked")
        var subjectKeyHashFunction = (Function<Request<T>, Object>) subjectKeysByRequestType.getOrDefault(request.body().getClass(), Object::hashCode);
        return Optional.ofNullable(subjectKeyHashFunction.apply(request))
                .map(Object::hashCode)
                .orElse(0); // guarantee order of all if no subject key is provided
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
