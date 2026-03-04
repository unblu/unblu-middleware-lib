package com.unblu.middleware.outboundrequests.handler;

import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestQueueService;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

public interface OutboundHandler extends RequestQueueService {

    default <T, R> void on(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, R> responseFunction,
            Consumer<T> asyncHandler) {
        on(requestType, requestClass, responseClass, responseFunction, asyncHandler, OutboundRequestHandlerOptions.defaults());
    }

    default <T, R> void on(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, R> responseFunction,
            Consumer<T> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<T> outboundRequestHandlerOptions) {
        onMono(
                requestType,
                requestClass,
                responseClass,
                request -> Mono.just(responseFunction.apply(request)),
                asyncHandler == null ? null : request -> Mono.fromRunnable(() -> asyncHandler.accept(request)),
                outboundRequestHandlerOptions
        );
    }

    default <T, R> void onWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, R> responseFunction,
            Consumer<Request<T>> asyncHandler) {
        onWrapped(requestType, requestClass, responseClass, responseFunction, asyncHandler, OutboundRequestHandlerOptions.defaults());
    }

    default <T, R> void onWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, R> responseFunction,
            Consumer<Request<T>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions) {
        onWrappedMono(
                requestType,
                requestClass,
                responseClass,
                request -> Mono.just(responseFunction.apply(request)),
                asyncHandler == null ? null : request -> Mono.fromRunnable(() -> asyncHandler.accept(request)),
                outboundRequestHandlerOptions
        );
    }


    default <T, R> void onMono(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, Mono<R>> responseFunction,
            Function<T, Mono<Void>> asyncHandler) {
        onMono(requestType, requestClass, responseClass, responseFunction, asyncHandler, OutboundRequestHandlerOptions.defaults());
    }

    default <T, R> void onMono(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<T, Mono<R>> responseFunction,
            Function<T, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<T> outboundRequestHandlerOptions) {
        Function<Request<T>, Mono<Void>> wrappedAsyncHandler = asyncHandler == null ? null : wrapped(asyncHandler);
        onWrappedMono(
                requestType,
                requestClass,
                responseClass,
                wrapped(responseFunction),
                wrappedAsyncHandler,
                wrapped(outboundRequestHandlerOptions)
        );
    }

    default <T, R> void onWrappedMono(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler) {
        onWrappedMono(requestType, requestClass, responseClass, responseFunction, asyncHandler, OutboundRequestHandlerOptions.defaults());
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T, R> void onMonoWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler) {
        onWrappedMono(requestType, requestClass, responseClass, responseFunction, asyncHandler);
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T, R> void onMonoWrapped(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions) {
        onWrappedMono(requestType, requestClass, responseClass, responseFunction, asyncHandler, outboundRequestHandlerOptions);
    }

    <T, R> void onWrappedMono(
            @NonNull OutboundRequestType requestType,
            @NonNull Class<T> requestClass,
            @NonNull Class<R> responseClass,
            @NonNull Function<Request<T>, Mono<R>> responseFunction,
            Function<Request<T>, Mono<Void>> asyncHandler,
            @NonNull OutboundRequestHandlerOptions<Request<T>> outboundRequestHandlerOptions);

    <T> Mono<Object> handle(OutboundRequestType requestType, Request<T> request);

    Mono<Object> handle(OutboundRequestType requestType, byte[] body, RawRequest request);
}
