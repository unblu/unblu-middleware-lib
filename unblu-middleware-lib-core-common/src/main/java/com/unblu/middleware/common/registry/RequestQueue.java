package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

@Slf4j
public class RequestQueue {
    private final ContextRegistryWrapper contextRegistryWrapper;
    // multicast with an unbounded buffer: requests queued before the first subscriber attaches are
    // buffered, delivered requests are not retained (replay().all() kept every request for the
    // process lifetime), and additional subscribers remain permitted
    private final Sinks.Many<Request<?>> sink = Sinks.many().multicast().onBackpressureBuffer(Integer.MAX_VALUE, false);
    private final Sinks.One<Integer> shutDownSink = Sinks.one();

    private final Map<Class<?>, Function<?, Object>> subjectKeysByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Actions<?>> actionsByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, ContextSpec<?>> contextEntriesByRequestType = new ConcurrentHashMap<>();

    private final RequestQueueErrorHandler requestQueueErrorHandler;

    private static final long DRAIN_TIMEOUT_SECONDS = 30;
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final CountDownLatch drained = new CountDownLatch(1);
    private volatile boolean everSubscribed = false;

    @Getter
    private final Flux<Void> flux;

    public RequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper, RequestQueueErrorHandler requestQueueErrorHandler) {
        this.contextRegistryWrapper = contextRegistryWrapper;
        this.requestQueueErrorHandler = requestQueueErrorHandler;
        this.flux = sink.asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnError(_e -> fatalStartupErrorHandler.shutdown())
                .takeUntilOther(shutDownSink.asMono())
                .groupBy(it -> Math.floorMod(subjectKeyHash(it), 100)) // max 100 parallel (floorMod: % would double the group count via negative keys)
                .flatMap(f -> f
                        .publishOn(Schedulers.boundedElastic())
                        // concatMap: requests with the same subject key are processed strictly
                        // one after another, in arrival order — also for async handlers.
                        // Mono.defer + onErrorResume: a synchronous throw from user code
                        // (context spec, handler lambda) must skip this request, never
                        // terminate the shared pipeline.
                        .concatMap(request -> Mono.defer(() -> processRequest(request))
                                .onErrorResume(e -> {
                                    log.error("Unexpected error while processing request of type {}; skipping it",
                                            request.body().getClass().getSimpleName(), e);
                                    return Mono.empty();
                                })))
                .doOnSubscribe(_s -> everSubscribed = true)
                .doOnComplete(drained::countDown);
    }

    public <T> void queueRequest(Request<T> request) {
        sink.emitNext(request, this::emitFailureHandler);
    }

    public <T> void on(Class<T> requestType, Function<T, Mono<Void>> action, RequestOrderSpec<T> requestOrderSpec, ContextSpec<T> contextSpec) {
        onWrapped(requestType, wrapped(action), wrapped(requestOrderSpec), wrapped(contextSpec));
    }

    @SuppressWarnings("unchecked")
    public <T> void onWrapped(Class<T> requestType, Function<Request<T>, Mono<Void>> action, RequestOrderSpec<Request<T>> requestOrderSpec, ContextSpec<Request<T>> contextSpec) {
        contextRegistryWrapper.registerContextSpec(contextSpec);
        ((Actions<T>) actionsByRequestType.computeIfAbsent(requestType, _k -> Actions.empty())).add(action);
        subjectKeysByRequestType.put(requestType, requestOrderSpec.keyExtractor());
        contextEntriesByRequestType.put(requestType, contextSpec);
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<Void> processRequest(Request<T> request) {
        var requestType = request.body().getClass();
        var actions = (Actions<T>) actionsByRequestType.getOrDefault(requestType, Actions.empty());
        var contextSpec = (ContextSpec<Request<T>>) contextEntriesByRequestType.getOrDefault(requestType, ContextSpec.empty());

        return Flux.fromIterable(actions.actions())
                .flatMap(action ->
                        // defer: a synchronous throw from the handler must reach the error
                        // handler the same way as a returned Mono.error
                        Mono.defer(() -> action.apply(request))
                                .doOnError(e -> log.error(e.getMessage(), e))
                                .onErrorResume(requestQueueErrorHandler::handleError))
                .contextWrite(contextSpec.applyTo(request))
                .then();
    }

    private <T> int subjectKeyHash(Request<T> request) {
        @SuppressWarnings("unchecked")
        var subjectKeyHashFunction = (Function<Request<T>, Object>) subjectKeysByRequestType.getOrDefault(request.body().getClass(), Object::hashCode);
        try {
            return Optional.ofNullable(subjectKeyHashFunction.apply(request))
                    .map(Object::hashCode)
                    .orElse(0); // guarantee order of all if no subject key is provided
        } catch (RuntimeException e) {
            // a throwing key extractor runs inside the groupBy selector and would otherwise
            // terminate the shared pipeline for every handler
            log.warn("Subject key extractor threw for request type {}; falling back to the shared key",
                    request.body().getClass().getSimpleName(), e);
            return 0;
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
        // idempotent: apps legitimately call shutdown() themselves before @PreDestroy runs
        if (!shutdownInitiated.compareAndSet(false, true)) {
            log.debug("RequestQueue shutdown already initiated");
            return;
        }
        // completing the sink stops intake while already-queued requests still drain to the
        // subscriber; requests dropped here were HTTP-acknowledged and would be lost silently
        sink.tryEmitComplete();
        try {
            if (everSubscribed && !drained.await(DRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("RequestQueue did not drain within {} seconds; remaining queued requests are dropped", DRAIN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        shutDownSink.tryEmitValue(0); // hard stop for anything still in flight
        log.info("RequestQueue has been shut down");
    }
}
