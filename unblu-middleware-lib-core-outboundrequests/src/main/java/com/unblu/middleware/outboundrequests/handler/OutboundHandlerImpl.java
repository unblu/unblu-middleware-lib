package com.unblu.middleware.outboundrequests.handler;

import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Named
@Singleton
@RequiredArgsConstructor
public class OutboundHandlerImpl implements OutboundHandler {

    private final OutboundRequestHandler outboundRequestHandler;

    @Override
    public <T, R> void onWrappedMono(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions) {
        outboundRequestHandler.onWrappedMono(
                requestType,
                requestClass,
                responseClass,
                responseFunction,
                asyncHandler,
                outboundRequestHandlerOptions
        );
    }

    @Override
    public <T> Mono<Object> handle(OutboundRequestType requestType, Request<T> request) {
        return outboundRequestHandler.handle(requestType, request);
    }

    @Override
    public Mono<Object> handle(OutboundRequestType requestType, byte[] body, RawRequest request) {
        return outboundRequestHandler.handle(requestType, body, request);
    }

    @Override
    public Flux<Void> getFlux() {
        return outboundRequestHandler.getFlux();
    }

    @Override
    public void shutdown() {
        outboundRequestHandler.shutdown();
    }

    @Override
    public void subscribe() {
        outboundRequestHandler.subscribe();
    }

    @Override
    public boolean isSubscribed() {
        return outboundRequestHandler.isSubscribed();
    }
}
