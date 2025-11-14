package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestQueueService;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.mono;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;

// Use this interface name going forward
public interface WebhookHandler extends RequestQueueService {

    default <T> void onWebhook(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<T> processAction) {
        onWebhookMono(eventName, expectedType, mono(processAction));
    }

    default <T> void onWebhook(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<T> processAction,
                               @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onWebhookMono(eventName, expectedType, mono(processAction), webhookHandlerOptions);
    }

    default <T> void onWrappedWebhook(@NonNull String eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Consumer<Request<T>> processAction) {
        onWrappedWebhookMono(eventName, expectedType, mono(processAction));
    }

    default <T> void onWrappedWebhook(@NonNull String eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Consumer<Request<T>> processAction,
                                      @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedWebhookMono(eventName, expectedType, mono(processAction), webhookHandlerOptions);
    }


    default <T> void onWebhook(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<T> processAction) {
        onWebhookMono(eventName, expectedType, mono(processAction));
    }

    default <T> void onWebhook(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<T> processAction,
                               @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onWebhookMono(eventName, expectedType, mono(processAction), webhookHandlerOptions);
    }

    default <T> void onWrappedWebhook(@NonNull EventName eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Consumer<Request<T>> processAction) {
        onWrappedWebhookMono(eventName, expectedType, mono(processAction));
    }

    default <T> void onWrappedWebhook(@NonNull EventName eventName,
                                      @NonNull Class<T> expectedType,
                                      @NonNull Consumer<Request<T>> processAction,
                                      @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedWebhookMono(eventName, expectedType, mono(processAction), webhookHandlerOptions);
    }


    default <T> void onWebhookMono(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<T, Mono<Void>> processAction) {
        onWebhookMono(eventName(eventName), expectedType, processAction);
    }

    default <T> void onWebhookMono(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<T, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onWebhookMono(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }

    default <T> void onWrappedWebhookMono(@NonNull String eventName,
                                          @NonNull Class<T> expectedType,
                                          @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedWebhookMono(eventName(eventName), expectedType, processAction);
    }

    default <T> void onWrappedWebhookMono(@NonNull String eventName,
                                          @NonNull Class<T> expectedType,
                                          @NonNull Function<Request<T>, Mono<Void>> processAction,
                                          @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedWebhookMono(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }


    default <T> void onWebhookMono(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<T, Mono<Void>> processAction) {
        onWebhookMono(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    default <T> void onWebhookMono(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<T, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onWrappedWebhookMono(eventName, expectedType, wrapped(processAction), wrapped(webhookHandlerOptions));
    }

    default <T> void onWrappedWebhookMono(@NonNull EventName eventName,
                                          @NonNull Class<T> expectedType,
                                          @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedWebhookMono(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    <T> void onWrappedWebhookMono(@NonNull EventName eventName,
                                  @NonNull Class<T> expectedType,
                                  @NonNull Function<Request<T>, Mono<Void>> processAction,
                                  @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions);
}
