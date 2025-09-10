package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueueService;
import com.unblu.middleware.webhooks.entity.EventName;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
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
        onWebhook(eventName, expectedType, processAction, requestOrderSpec, List.of());
    }

    default <T> void onWebhook(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec,
                               @NonNull Collection<ContextEntrySpec<T>> contextEntries) {
        onWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec, contextEntries);
    }

    default <T> void onWebhook(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Function<T, Mono<Void>> processAction,
                               @NonNull RequestOrderSpec<T> requestOrderSpec,
                               @NonNull Collection<ContextEntrySpec<T>> contextEntries) {
        onWrappedWebhook(eventName, expectedType, wrapped(processAction), wrapped(requestOrderSpec), wrapped(contextEntries));
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
        onWrappedWebhook(eventName, expectedType, processAction, requestOrderSpec, List.of());
    }

    default <T> void onWrappedWebhook(@NonNull String eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Function<Request<T>, Mono<Void>> processAction,
                                      @NonNull RequestOrderSpec<Request<T>> requestOrderSpec,
                                      @NonNull Collection<ContextEntrySpec<Request<T>>> contextEntries) {
        onWrappedWebhook(eventName(eventName), expectedType, processAction, requestOrderSpec, contextEntries);
    }

    <T> void onWrappedWebhook(@NonNull EventName eventName,
                              @NonNull Class<T> expectedType,
                              @NonNull Function<Request<T>, Mono<Void>> processAction,
                              @NonNull RequestOrderSpec<Request<T>> requestOrderSpec,
                              @NonNull Collection<ContextEntrySpec<Request<T>>> contextEntries);
}
