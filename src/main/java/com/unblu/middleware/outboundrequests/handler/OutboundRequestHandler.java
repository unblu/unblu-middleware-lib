package com.unblu.middleware.outboundrequests.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrappedHeaderSpec;
import static com.unblu.middleware.outboundrequests.util.OutboundRequestsContextSpecUtil.outboundRequestHeadersContextSpec;

@Service
@Slf4j
public class OutboundRequestHandler extends RequestQueueServiceImpl {

    private final ObjectMapper objectMapper;
    private final ContextRegistryWrapper contextRegistryWrapper;
    private final Map<Class<?>, Function<Request<?>, Mono<?>>> responseByRequestType = new ConcurrentHashMap<>();
    private final Map<OutboundRequestType, Class<?>> requestClassByRequestType = new ConcurrentHashMap<>();
    private final Map<Class<?>, ContextSpec<?>> contextEntriesByRequestType = new ConcurrentHashMap<>();

    public OutboundRequestHandler(RequestQueue requestQueue, ObjectMapper objectMapper, ContextRegistryWrapper contextRegistryWrapper) {
        super(requestQueue);
        this.objectMapper = objectMapper;
        this.contextRegistryWrapper = contextRegistryWrapper;
    }

    public <T, R> void on(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, Mono<R>> responseFunction,
            Function<T, Mono<Void>> asyncHandler,
            RequestOrderSpec<T> requestOrderSpec,
            @NonNull ContextSpec<T> contextSpec) {
        onWrapped(requestType, requestClass, responseClass, wrapped(responseFunction), wrapped(asyncHandler), wrapped(requestOrderSpec), wrapped(contextSpec));
    }

    public <T, R> void onWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            RequestOrderSpec<Request<T>> requestOrderSpec,
            @NonNull ContextSpec<Request<T>> contextSpec) {
        registerHandler(requestType, requestClass, responseClass, responseFunction, asyncHandler, requestOrderSpec, contextSpec);
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
            RequestOrderSpec<Request<T>> requestOrderSpec,
            @NonNull ContextSpec<Request<T>> contextSpec) {
        var updatedContextSpec = contextSpec.with(wrappedHeaderSpec(outboundRequestHeadersContextSpec()));
        if (asyncHandler != null) {
            requestQueue.onWrapped(requestClass, asyncHandler, requestOrderSpec, updatedContextSpec);
        }
        contextRegistryWrapper.registerContextSpec(updatedContextSpec);
        contextEntriesByRequestType.put(requestClass, updatedContextSpec);
        responseByRequestType.put(requestClass, (Function) responseFunction);
        requestClassByRequestType.put(requestType, requestClass);
    }

    @SuppressWarnings("unchecked")
    public <T, R> Mono<R> handle(OutboundRequestType requestType, Request<T> request) {
        requestQueue.queueRequest(request);
        var requestClass = request.body().getClass();
        var function = responseByRequestType.get(requestClass);
        var contextSpec = (ContextSpec<Request<T>>) contextEntriesByRequestType.getOrDefault(requestClass, ContextSpec.empty());
        return Optional.ofNullable(function)
                .map(it -> (Mono<R>) it.apply(request))
                .orElseGet(() -> Mono.error(new NoHandlerException("No handler registered for outbound request type: " + requestType)))
                .doOnError(e -> log.error(e.getMessage()))
                .contextWrite(contextSpec.applyTo(request));
    }

    public Mono<Object> handle(OutboundRequestType requestType, byte[] body, ServerHttpRequest request) {
        try {
            var parsedRequest = objectMapper.readValue(body, requestClassByRequestType.get(requestType));
            return handle(requestType, new Request<>(parsedRequest, request.getHeaders()));
        } catch (IOException e) {
            throw new InvalidRequestException("Request of type %s could not be parsed".formatted(requestType), e);
        }
    }
}
