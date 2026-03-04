package com.unblu.middleware.outboundrequests.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.middleware.common.utils.ThrowingFunction;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import com.unblu.webapi.model.v4.PingRequest;
import com.unblu.webapi.model.v4.PingResponse;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrappedHeaderSpec;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static com.unblu.middleware.outboundrequests.util.OutboundRequestsContextSpecUtil.outboundRequestHeadersContextSpec;

@Named
@Singleton
@Slf4j
public class OutboundRequestHandler extends RequestQueueServiceImpl {

    private final ObjectMapper objectMapper;
    private final ContextRegistryWrapper contextRegistryWrapper;
    private final Map<Class<?>, Function<Request<?>, Mono<?>>> responseByRequestType = new ConcurrentHashMap<>();
    private final Map<OutboundRequestType, Class<?>> requestClassByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, ContextSpec<?>> contextEntriesByRequestType = new ConcurrentHashMap<>();

    public OutboundRequestHandler(OutboundRequestQueue outboundRequestQueue, ObjectMapper objectMapper, ContextRegistryWrapper contextRegistryWrapper) {
        super(outboundRequestQueue);
        this.objectMapper = objectMapper;
        this.contextRegistryWrapper = contextRegistryWrapper;
    }

    @PostConstruct
    private void registerOutboundPingHandler() {
        on(outboundRequestType("outbound.ping"), PingRequest.class, PingResponse.class,
                request -> Mono.just(new PingResponse().pingId(request.getPingId())),
                request -> Mono.fromRunnable(() -> log.info("Received ping outbound request")),
                OutboundRequestHandlerOptions.defaults());
    }

    public <T, R> void on(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, Mono<R>> responseFunction,
            Function<T, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<T> outboundRequestHandlerOptions) {
        Function<Request<T>, Mono<Void>> wrappedAsyncHandler = asyncHandler == null ? null : wrapped(asyncHandler);
        onWrapped(
                requestType,
                requestClass,
                responseClass,
                wrapped(responseFunction),
                wrappedAsyncHandler,
                wrapped(outboundRequestHandlerOptions)
        );
    }

    /**
     * @deprecated Use on(...) with OutboundRequestHandlerOptions
     */
    @Deprecated
    public <T, R> void on(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, Mono<R>> responseFunction,
            Function<T, Mono<Void>> asyncHandler,
            RequestOrderSpec<T> requestOrderSpec,
            @NonNull ContextSpec<T> contextSpec) {
        on(
                requestType,
                requestClass,
                responseClass,
                responseFunction,
                asyncHandler,
                OutboundRequestHandlerOptions.<T>defaults()
                        .withRequestOrderSpec(requestOrderSpec == null ? RequestOrderSpec.canIgnoreOrder() : requestOrderSpec)
                        .withContextSpec(contextSpec)
        );
    }

    public <T, R> void onWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions) {
        registerHandler(requestType, requestClass, responseClass, responseFunction, asyncHandler, outboundRequestHandlerOptions);
    }

    /**
     * @deprecated Use onWrapped(...) with OutboundRequestHandlerOptions
     */
    @Deprecated
    public <T, R> void onWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            RequestOrderSpec<Request<T>> requestOrderSpec,
            @NonNull ContextSpec<Request<T>> contextSpec) {
        onWrapped(
                requestType,
                requestClass,
                responseClass,
                responseFunction,
                asyncHandler,
                OutboundRequestHandlerOptions.<Request<T>>defaults()
                        .withRequestOrderSpec(requestOrderSpec == null ? RequestOrderSpec.canIgnoreOrder() : requestOrderSpec)
                        .withContextSpec(contextSpec)
        );
    }

    // Combination of requestType, requestClass and responseClass is given and is 1:1:1, just unknown to the lib :(
    // To register multiple handlers, you can call this multiple times for the given requestType (or requestClass or responseClass).
    // However in that case, only the responseFunction from the last call will actually be used. To avoid confusion,
    // the recommendation would be to always use effectively the same responseFunction in every such call.
    // ResponseClass is only set for validation purposes (to validate return type of the responseFunction lambda)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, R> void registerHandler(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions) {
        var updatedContextSpec = outboundRequestHandlerOptions.contextSpec().with(wrappedHeaderSpec(outboundRequestHeadersContextSpec()));
        if (asyncHandler != null) {
            requestQueue.onWrapped(requestClass, asyncHandler, outboundRequestHandlerOptions.requestOrderSpec(), updatedContextSpec);
        }
        contextRegistryWrapper.registerContextSpec(updatedContextSpec);
        contextEntriesByRequestType.put(requestClass, updatedContextSpec);
        responseByRequestType.put(requestClass, (Function) responseFunction);
        requestClassByRequestType.put(requestType, requestClass);
    }

    /**
     * @deprecated Use registerHandler(...) with OutboundRequestHandlerOptions
     */
    @Deprecated
    public <T, R> void registerHandler(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            RequestOrderSpec<Request<T>> requestOrderSpec,
            @NonNull ContextSpec<Request<T>> contextSpec) {
        registerHandler(
                requestType,
                requestClass,
                responseClass,
                responseFunction,
                asyncHandler,
                OutboundRequestHandlerOptions.<Request<T>>defaults()
                        .withRequestOrderSpec(requestOrderSpec == null ? RequestOrderSpec.canIgnoreOrder() : requestOrderSpec)
                        .withContextSpec(contextSpec)
        );
    }

    @SuppressWarnings("unchecked")
    public <T> Mono<Object> handle(OutboundRequestType requestType, Request<T> request) {
        requestQueue.queueRequest(request);
        var requestClass = request.body().getClass();
        var function = responseByRequestType.get(requestClass);
        var contextSpec = (ContextSpec<Request<T>>) contextEntriesByRequestType.getOrDefault(requestClass, ContextSpec.empty());
        return Mono.justOrEmpty(function)
                .switchIfEmpty(Mono.error(new NoHandlerException("No handler registered for outbound request type: " + requestType)))
                .flatMap(it -> (Mono<Object>) it.apply(request))
                .contextWrite(contextSpec.applyTo(request));
    }

    public Mono<Object> handle(OutboundRequestType requestType, byte[] body, RawRequest request) {
        return Mono.justOrEmpty(requestClassByRequestType.get(requestType))
                .switchIfEmpty(Mono.error(new NoHandlerException("No handler registered for outbound request type: " + requestType)))
                .map((ThrowingFunction<Class<?>, ?>) type -> objectMapper.readValue(body, type))
                .flatMap(parsed -> handle(requestType, new Request<>(parsed, request.headers())));
    }
}
