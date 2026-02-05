package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

public interface DialogBotService {

    default void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIf(wrapped(condition));
    }
    default void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIf(wrapped(condition));
    }
    default void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIf(wrapped(condition));
    }

    default void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptWrappedOnboardingOfferIf(wrapped(condition), wrapped(contextSpec));
    }
    default void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptWrappedReboardingOfferIf(wrapped(condition), wrapped(contextSpec));
    }
    default void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptWrappedOffboardingOfferIf(wrapped(condition), wrapped(contextSpec));
    }

    default void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIf(condition, ContextSpec.empty());
    }
    default void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIf(condition, ContextSpec.empty());
    }
    default void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIf(condition, ContextSpec.empty());
    }

    void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec);
    void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec);
    void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec);

    default void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action) {
        onDialogOpen(action, ContextSpec.empty());
    }
    default void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action) {
        onDialogMessage(action, ContextSpec.empty());
    }
    default void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action) {
        onDialogMessageState(action, ContextSpec.empty());
    }
    default void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action) {
        onDialogCounterpartChanged(action, ContextSpec.empty());
    }
    default void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action) {
        onDialogClosed(action, ContextSpec.empty());
    }

    default void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onWrappedDialogOpen(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onWrappedDialogMessage(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onWrappedDialogMessageState(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onWrappedDialogCounterpartChanged(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onWrappedDialogClosed(wrapped(action), wrapped(contextSpec));
    }

    void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec);
    void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec);
    void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec);
    void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec);
    void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec);

    void assertSubscribed();
}
