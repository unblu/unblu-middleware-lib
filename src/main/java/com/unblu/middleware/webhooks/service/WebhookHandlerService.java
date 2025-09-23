package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueueService;
import com.unblu.middleware.webhooks.entity.EventName;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;

public interface WebhookHandlerService extends RequestQueueService {

    default <T> void onWebhook(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec) {
        onWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec);
    }

    default <T> void onWebhook(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec) {
        onWebhook(eventName, expectedType, processAction, requestOrderSpec, ContextSpec.empty());
    }

    default <T> void onWebhook(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec,
                               @NonNull ContextSpec<T> contextSpec) {
        onWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec, contextSpec);
    }

    default <T> void onWebhook(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec,
                               @NonNull ContextSpec<T> contextSpec) {
        onWrappedWebhook(eventName, expectedType, wrapped(processAction), wrapped(requestOrderSpec), wrapped(contextSpec));
    }

    default <T> void onWrappedWebhook(@NonNull String eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Function<Request<T>, Mono<Void>> processAction,
                                      @NonNull RequestOrderSpec<Request<T>> requestOrderSpec) {
        onWrappedWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec);
    }

    default <T> void onWrappedWebhook(@NonNull EventName eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Function<Request<T>, Mono<Void>> processAction,
                                      @NonNull RequestOrderSpec<Request<T>> requestOrderSpec) {
        onWrappedWebhook(eventName, expectedType, processAction, requestOrderSpec, ContextSpec.empty());
    }

    default <T> void onWrappedWebhook(@NonNull String eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Function<Request<T>, Mono<Void>> processAction,
                                      @NonNull RequestOrderSpec<Request<T>> requestOrderSpec,
                                      @NonNull ContextSpec<Request<T>> contextSpec) {
        onWrappedWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec, contextSpec);
    }

    <T> void onWrappedWebhook(@NonNull EventName eventName,
                              @NonNull Class<T> expectedType,
                              @NonNull Function<Request<T>, Mono<Void>> processAction,
                              @NonNull RequestOrderSpec<Request<T>> requestOrderSpec,
                              @NonNull ContextSpec<Request<T>> contextSpec);
}
