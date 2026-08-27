package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.*;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface DialogBot {

    default void acceptOnboardingOfferIf(Predicate<BotOnboardingOfferRequest> condition) {
        acceptOnboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptReboardingOfferIf(Predicate<BotReboardingOfferRequest> condition) {
        acceptReboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptOffboardingOfferIf(Predicate<BotOffboardingOfferRequest> condition) {
        acceptOffboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptOnboardingOfferIf(Predicate<BotOnboardingOfferRequest> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptOnboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptReboardingOfferIf(Predicate<BotReboardingOfferRequest> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptReboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptOffboardingOfferIf(Predicate<BotOffboardingOfferRequest> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptOffboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptWrappedOnboardingOfferIf(Predicate<Request<BotOnboardingOfferRequest>> condition) {
        acceptWrappedOnboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptWrappedReboardingOfferIf(Predicate<Request<BotReboardingOfferRequest>> condition) {
        acceptWrappedReboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptWrappedOffboardingOfferIf(Predicate<Request<BotOffboardingOfferRequest>> condition) {
        acceptWrappedOffboardingOfferIf(condition, ContextSpec.empty());
    }

    default void acceptWrappedOnboardingOfferIf(Predicate<Request<BotOnboardingOfferRequest>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        acceptWrappedOnboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptWrappedReboardingOfferIf(Predicate<Request<BotReboardingOfferRequest>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        acceptWrappedReboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptWrappedOffboardingOfferIf(Predicate<Request<BotOffboardingOfferRequest>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        acceptWrappedOffboardingOfferIfMono(event -> Mono.just(condition.test(event)), contextSpec);
    }

    default void acceptOnboardingOfferIfMono(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        acceptOnboardingOfferIfMono(condition, ContextSpec.empty());
    }

    default void acceptReboardingOfferIfMono(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        acceptReboardingOfferIfMono(condition, ContextSpec.empty());
    }

    default void acceptOffboardingOfferIfMono(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        acceptOffboardingOfferIfMono(condition, ContextSpec.empty());
    }

    void acceptOnboardingOfferIfMono(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec);
    void acceptReboardingOfferIfMono(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec);
    void acceptOffboardingOfferIfMono(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec);

    default void acceptWrappedOnboardingOfferIfMono(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIfMono(condition, ContextSpec.empty());
    }

    default void acceptWrappedReboardingOfferIfMono(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIfMono(condition, ContextSpec.empty());
    }

    default void acceptWrappedOffboardingOfferIfMono(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIfMono(condition, ContextSpec.empty());
    }

    void acceptWrappedOnboardingOfferIfMono(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec);
    void acceptWrappedReboardingOfferIfMono(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec);
    void acceptWrappedOffboardingOfferIfMono(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec);

    /**
     * @deprecated Renamed to acceptOnboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        acceptOnboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptReboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        acceptReboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptOffboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        acceptOffboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptOnboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptOnboardingOfferIfMono(condition, contextSpec);
    }

    /**
     * @deprecated Renamed to acceptReboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptReboardingOfferIfMono(condition, contextSpec);
    }

    /**
     * @deprecated Renamed to acceptOffboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptOffboardingOfferIfMono(condition, contextSpec);
    }

    /**
     * @deprecated Renamed to acceptWrappedOnboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptWrappedReboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptWrappedOffboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIfMono(condition);
    }

    /**
     * @deprecated Renamed to acceptWrappedOnboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        acceptWrappedOnboardingOfferIfMono(condition, contextSpec);
    }

    /**
     * @deprecated Renamed to acceptWrappedReboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        acceptWrappedReboardingOfferIfMono(condition, contextSpec);
    }

    /**
     * @deprecated Renamed to acceptWrappedOffboardingOfferIfMono(...)
     */
    @Deprecated
    default void acceptMonoWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        acceptWrappedOffboardingOfferIfMono(condition, contextSpec);
    }


    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogOpen(Consumer<BotDialogOpenRequest> action) {
        onDialogOpen(action, ContextSpec.empty());
    }

    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogMessage(Consumer<BotDialogMessageRequest> action) {
        onDialogMessage(action, ContextSpec.empty());
    }

    default void onDialogMessageState(Consumer<BotDialogMessageStateRequest> action) {
        onDialogMessageState(action, ContextSpec.empty());
    }

    default void onDialogCounterpartChanged(Consumer<BotDialogCounterpartChangedRequest> action) {
        onDialogCounterpartChanged(action, ContextSpec.empty());
    }

    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogClosed(Consumer<BotDialogClosedRequest> action) {
        onDialogClosed(action, ContextSpec.empty());
    }

    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogOpen(Consumer<BotDialogOpenRequest> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onDialogOpenMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogMessage(Consumer<BotDialogMessageRequest> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onDialogMessageMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onDialogMessageState(Consumer<BotDialogMessageStateRequest> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onDialogMessageStateMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onDialogCounterpartChanged(Consumer<BotDialogCounterpartChangedRequest> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onDialogCounterpartChangedMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    /**
     * Registers a synchronous handler. The consumer's return value (if the lambda body is an
     * expression) is DISCARDED — in particular, a returned {@code Mono} is never subscribed.
     * Handlers migrated from 1.x {@code Function<T, Mono<Void>>} must use the {@code *Mono}
     * variant instead, otherwise they silently do nothing.
     */
    default void onDialogClosed(Consumer<BotDialogClosedRequest> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onDialogClosedMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onWrappedDialogOpen(Consumer<Request<BotDialogOpenRequest>> action) {
        onWrappedDialogOpen(action, ContextSpec.empty());
    }

    default void onWrappedDialogMessage(Consumer<Request<BotDialogMessageRequest>> action) {
        onWrappedDialogMessage(action, ContextSpec.empty());
    }

    default void onWrappedDialogMessageState(Consumer<Request<BotDialogMessageStateRequest>> action) {
        onWrappedDialogMessageState(action, ContextSpec.empty());
    }

    default void onWrappedDialogCounterpartChanged(Consumer<Request<BotDialogCounterpartChangedRequest>> action) {
        onWrappedDialogCounterpartChanged(action, ContextSpec.empty());
    }

    default void onWrappedDialogClosed(Consumer<Request<BotDialogClosedRequest>> action) {
        onWrappedDialogClosed(action, ContextSpec.empty());
    }

    default void onWrappedDialogOpen(Consumer<Request<BotDialogOpenRequest>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        onWrappedDialogOpenMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onWrappedDialogMessage(Consumer<Request<BotDialogMessageRequest>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        onWrappedDialogMessageMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onWrappedDialogMessageState(Consumer<Request<BotDialogMessageStateRequest>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        onWrappedDialogMessageStateMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onWrappedDialogCounterpartChanged(Consumer<Request<BotDialogCounterpartChangedRequest>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        onWrappedDialogCounterpartChangedMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onWrappedDialogClosed(Consumer<Request<BotDialogClosedRequest>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        onWrappedDialogClosedMono(event -> Mono.fromRunnable(() -> action.accept(event)), contextSpec);
    }

    default void onDialogOpenMono(Function<BotDialogOpenRequest, Mono<Void>> action) {
        onDialogOpenMono(action, ContextSpec.empty());
    }

    default void onDialogMessageMono(Function<BotDialogMessageRequest, Mono<Void>> action) {
        onDialogMessageMono(action, ContextSpec.empty());
    }

    default void onDialogMessageStateMono(Function<BotDialogMessageStateRequest, Mono<Void>> action) {
        onDialogMessageStateMono(action, ContextSpec.empty());
    }

    default void onDialogCounterpartChangedMono(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action) {
        onDialogCounterpartChangedMono(action, ContextSpec.empty());
    }

    default void onDialogClosedMono(Function<BotDialogClosedRequest, Mono<Void>> action) {
        onDialogClosedMono(action, ContextSpec.empty());
    }

    void onDialogOpenMono(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec);
    void onDialogMessageMono(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec);
    void onDialogMessageStateMono(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec);
    void onDialogCounterpartChangedMono(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec);
    void onDialogClosedMono(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec);


    default void onWrappedDialogOpenMono(Function<Request<BotDialogOpenRequest>, Mono<Void>> action) {
        onWrappedDialogOpenMono(action, ContextSpec.empty());
    }

    default void onWrappedDialogMessageMono(Function<Request<BotDialogMessageRequest>, Mono<Void>> action) {
        onWrappedDialogMessageMono(action, ContextSpec.empty());
    }

    default void onWrappedDialogMessageStateMono(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action) {
        onWrappedDialogMessageStateMono(action, ContextSpec.empty());
    }

    default void onWrappedDialogCounterpartChangedMono(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action) {
        onWrappedDialogCounterpartChangedMono(action, ContextSpec.empty());
    }

    default void onWrappedDialogClosedMono(Function<Request<BotDialogClosedRequest>, Mono<Void>> action) {
        onWrappedDialogClosedMono(action, ContextSpec.empty());
    }

    void onWrappedDialogOpenMono(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec);
    void onWrappedDialogMessageMono(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec);
    void onWrappedDialogMessageStateMono(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec);
    void onWrappedDialogCounterpartChangedMono(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec);
    void onWrappedDialogClosedMono(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec);

    /**
     * @deprecated Renamed to onDialogOpenMono(...)
     */
    @Deprecated
    default void onMonoDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action) {
        onDialogOpenMono(action);
    }

    /**
     * @deprecated Renamed to onDialogMessageMono(...)
     */
    @Deprecated
    default void onMonoDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action) {
        onDialogMessageMono(action);
    }

    /**
     * @deprecated Renamed to onDialogMessageStateMono(...)
     */
    @Deprecated
    default void onMonoDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action) {
        onDialogMessageStateMono(action);
    }

    /**
     * @deprecated Renamed to onDialogCounterpartChangedMono(...)
     */
    @Deprecated
    default void onMonoDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action) {
        onDialogCounterpartChangedMono(action);
    }

    /**
     * @deprecated Renamed to onDialogClosedMono(...)
     */
    @Deprecated
    default void onMonoDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action) {
        onDialogClosedMono(action);
    }

    /**
     * @deprecated Renamed to onDialogOpenMono(...)
     */
    @Deprecated
    default void onMonoDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onDialogOpenMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onDialogMessageMono(...)
     */
    @Deprecated
    default void onMonoDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onDialogMessageMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onDialogMessageStateMono(...)
     */
    @Deprecated
    default void onMonoDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onDialogMessageStateMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onDialogCounterpartChangedMono(...)
     */
    @Deprecated
    default void onMonoDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onDialogCounterpartChangedMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onDialogClosedMono(...)
     */
    @Deprecated
    default void onMonoDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onDialogClosedMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onWrappedDialogOpenMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action) {
        onWrappedDialogOpenMono(action);
    }

    /**
     * @deprecated Renamed to onWrappedDialogMessageMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action) {
        onWrappedDialogMessageMono(action);
    }

    /**
     * @deprecated Renamed to onWrappedDialogMessageStateMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action) {
        onWrappedDialogMessageStateMono(action);
    }

    /**
     * @deprecated Renamed to onWrappedDialogCounterpartChangedMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action) {
        onWrappedDialogCounterpartChangedMono(action);
    }

    /**
     * @deprecated Renamed to onWrappedDialogClosedMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action) {
        onWrappedDialogClosedMono(action);
    }

    /**
     * @deprecated Renamed to onWrappedDialogOpenMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        onWrappedDialogOpenMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onWrappedDialogMessageMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        onWrappedDialogMessageMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onWrappedDialogMessageStateMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        onWrappedDialogMessageStateMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onWrappedDialogCounterpartChangedMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        onWrappedDialogCounterpartChangedMono(action, contextSpec);
    }

    /**
     * @deprecated Renamed to onWrappedDialogClosedMono(...)
     */
    @Deprecated
    default void onMonoWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        onWrappedDialogClosedMono(action, contextSpec);
    }

    void assertSubscribed();
}
