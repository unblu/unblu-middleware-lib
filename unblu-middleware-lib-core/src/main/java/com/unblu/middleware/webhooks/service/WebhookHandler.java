package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;

public interface WebhookHandler extends WebhookHandlerService {

    default <T> void on(@NonNull String eventName,
                        @NonNull Class<T> expectedType,
                        @NonNull Consumer<T> processAction) {
        on(eventName(eventName), expectedType, processAction);
    }

    default <T> void on(@NonNull String eventName,
                        @NonNull Class<T> expectedType,
                        @NonNull Consumer<T> processAction,
                        @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        on(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }

    default <T> void onWrapped(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<Request<T>> processAction) {
        onWrapped(eventName(eventName), expectedType, processAction);
    }

    default <T> void onWrapped(@NonNull String eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<Request<T>> processAction,
                               @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrapped(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }

    default <T> void on(@NonNull EventName eventName,
                        @NonNull Class<T> expectedType,
                        @NonNull Consumer<T> processAction) {
        on(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    default <T> void on(@NonNull EventName eventName,
                        @NonNull Class<T> expectedType,
                        @NonNull Consumer<T> processAction,
                        @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onMono(eventName, expectedType, event -> Mono.fromRunnable(() -> processAction.accept(event)), webhookHandlerOptions);
    }

    default <T> void onWrapped(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<Request<T>> processAction) {
        onWrapped(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    default <T> void onWrapped(@NonNull EventName eventName,
                               @NonNull Class<T> expectedType,
                               @NonNull Consumer<Request<T>> processAction,
                               @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedMono(eventName, expectedType, request -> Mono.fromRunnable(() -> processAction.accept(request)), webhookHandlerOptions);
    }


    default <T> void onMono(@NonNull String eventName,
                            @NonNull Class<T> expectedType,
                            @NonNull Function<T, Mono<Void>> processAction) {
        onMono(eventName(eventName), expectedType, processAction);
    }

    default <T> void onMono(@NonNull String eventName,
                            @NonNull Class<T> expectedType,
                            @NonNull Function<T, Mono<Void>> processAction,
                            @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onMono(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }

    default <T> void onWrappedMono(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedMono(eventName(eventName), expectedType, processAction);
    }

    default <T> void onWrappedMono(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedMono(eventName(eventName), expectedType, processAction, webhookHandlerOptions);
    }

    default <T> void onMono(@NonNull EventName eventName,
                            @NonNull Class<T> expectedType,
                            @NonNull Function<T, Mono<Void>> processAction) {
        onMono(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    default <T> void onMono(@NonNull EventName eventName,
                            @NonNull Class<T> expectedType,
                            @NonNull Function<T, Mono<Void>> processAction,
                            @NonNull WebhookHandlerOptions<T> webhookHandlerOptions) {
        onWrappedMono(eventName, expectedType, wrapped(processAction), wrapped(webhookHandlerOptions));
    }

    default <T> void onWrappedMono(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedMono(eventName, expectedType, processAction, WebhookHandlerOptions.defaults());
    }

    default <T> void onWrappedMono(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedWebhook(eventName, expectedType, processAction, webhookHandlerOptions);
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T> void onMonoWrapped(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedMono(eventName, expectedType, processAction);
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T> void onMonoWrapped(@NonNull String eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedMono(eventName, expectedType, processAction, webhookHandlerOptions);
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T> void onMonoWrapped(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction) {
        onWrappedMono(eventName, expectedType, processAction);
    }

    /**
     * @deprecated Renamed to onWrappedMono(...)
     */
    @Deprecated
    default <T> void onMonoWrapped(@NonNull EventName eventName,
                                   @NonNull Class<T> expectedType,
                                   @NonNull Function<Request<T>, Mono<Void>> processAction,
                                   @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        onWrappedMono(eventName, expectedType, processAction, webhookHandlerOptions);
    }
}
