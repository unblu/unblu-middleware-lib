package com.unblu.middleware.outboundrequests.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import org.springframework.stereotype.Service;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class OutboundRequestHandler extends RequestQueueServiceImpl {

    private final ObjectMapper objectMapper;
    private final Map<Class<?>, Function<Request<?>, Mono<?>>> responseByRequestType = new ConcurrentHashMap<>();
    private final Map<OutboundRequestType, Class<?>> requestClassByRequestType = new ConcurrentHashMap<>();

    public OutboundRequestHandler(RequestQueue requestQueue, ObjectMapper objectMapper) {
        super(requestQueue);
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, R> void registerHandler(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            RequestOrderSpec<Request<T>> requestOrderSpec,
            @NonNull Collection<ContextEntrySpec<Request<T>>> contextEntries) {
        if (asyncHandler != null) {
            requestQueue.onWrapped(requestClass, asyncHandler, requestOrderSpec, contextEntries);
        }
        responseByRequestType.put(requestClass, (Function) responseFunction);
        requestClassByRequestType.put(requestType, requestClass);
    }

    @SuppressWarnings("unchecked")
    public <T, R> Mono<R> handle(Request<T> request) {
        requestQueue.queueRequest(request);
        var function = responseByRequestType.get(request.body().getClass());
        return (Mono<R>) function.apply(request);
    }

    @SneakyThrows
    public Mono<Object> handle(OutboundRequestType requestType, byte[] body, ServerHttpRequest request) {
        var parsedRequest = objectMapper.readValue(body, requestClassByRequestType.get(requestType));
        return handle(new Request<>(parsedRequest, request.getHeaders()));
    }
}
