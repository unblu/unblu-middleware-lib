package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.*;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.mono;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

public interface DialogBot {

    default void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Boolean> condition) {
        acceptOnboardingOfferIfMono(mono(condition));
    }
    default void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Boolean> condition) {
        acceptReboardingOfferIfMono(mono(condition));
    }
    default void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Boolean> condition) {
        acceptOffboardingOfferIfMono(mono(condition));
    }

    default void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Boolean> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptOnboardingOfferIfMono(mono(condition), contextSpec);
    }
    default void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Boolean> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptReboardingOfferIfMono(mono(condition), contextSpec);
    }
    default void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Boolean> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptOffboardingOfferIfMono(mono(condition), contextSpec);
    }

    default void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Boolean> condition) {
        acceptWrappedOnboardingOfferIfMono(mono(condition));
    }
    default void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Boolean> condition) {
        acceptWrappedReboardingOfferIfMono(mono(condition));
    }
    default void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Boolean> condition) {
        acceptWrappedOffboardingOfferIfMono(mono(condition));
    }

    default void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Boolean> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        acceptWrappedOnboardingOfferIfMono(mono(condition), contextSpec);
    }
    default void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Boolean> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        acceptWrappedReboardingOfferIfMono(mono(condition), contextSpec);
    }
    default void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Boolean> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        acceptWrappedOffboardingOfferIfMono(mono(condition), contextSpec);
    }

    default void onDialogOpen(Consumer<BotDialogOpenRequest> action) {
        onDialogOpenMono(mono(action));
    }
    default void onDialogMessage(Consumer<BotDialogMessageRequest> action) {
        onDialogMessageMono(mono(action));
    }
    default void onDialogMessageState(Consumer<BotDialogMessageStateRequest> action) {
        onDialogMessageStateMono(mono(action));
    }
    default void onDialogCounterpartChanged(Consumer<BotDialogCounterpartChangedRequest> action) {
        onDialogCounterpartChangedMono(mono(action));
    }
    default void onDialogClosed(Consumer<BotDialogClosedRequest> action) {
        onDialogClosedMono(mono(action));
    }

    default void onDialogOpen(Consumer<BotDialogOpenRequest> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onDialogOpenMono(mono(action), contextSpec);
    }
    default void onDialogMessage(Consumer<BotDialogMessageRequest> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onDialogMessageMono(mono(action), contextSpec);
    }
    default void onDialogMessageState(Consumer<BotDialogMessageStateRequest> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onDialogMessageStateMono(mono(action), contextSpec);
    }
    default void onDialogCounterpartChanged(Consumer<BotDialogCounterpartChangedRequest> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onDialogCounterpartChangedMono(mono(action), contextSpec);
    }
    default void onDialogClosed(Consumer<BotDialogClosedRequest> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onDialogClosedMono(mono(action), contextSpec);
    }

    default void onWrappedDialogOpen(Consumer<Request<BotDialogOpenRequest>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        onWrappedDialogOpenMono(mono(action), contextSpec);
    }
    default void onWrappedDialogMessage(Consumer<Request<BotDialogMessageRequest>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        onWrappedDialogMessageMono(mono(action), contextSpec);
    }
    default void onWrappedDialogMessageState(Consumer<Request<BotDialogMessageStateRequest>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        onWrappedDialogMessageStateMono(mono(action), contextSpec);
    }
    default void onWrappedDialogCounterpartChanged(Consumer<Request<BotDialogCounterpartChangedRequest>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        onWrappedDialogCounterpartChangedMono(mono(action), contextSpec);
    }
    default void onWrappedDialogClosed(Consumer<Request<BotDialogClosedRequest>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        onWrappedDialogClosedMono(mono(action), contextSpec);
    }







    default void acceptOnboardingOfferIfMono(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIfMono(wrapped(condition));
    }
    default void acceptReboardingOfferIfMono(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIfMono(wrapped(condition));
    }
    default void acceptOffboardingOfferIfMono(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIfMono(wrapped(condition));
    }

    default void acceptOnboardingOfferIfMono(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptWrappedOnboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }
    default void acceptReboardingOfferIfMono(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptWrappedReboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }
    default void acceptOffboardingOfferIfMono(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptWrappedOffboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }

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

    default void onDialogOpenMono(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onWrappedDialogOpenMono(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogMessageMono(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onWrappedDialogMessageMono(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogMessageStateMono(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onWrappedDialogMessageStateMono(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogCounterpartChangedMono(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onWrappedDialogCounterpartChangedMono(wrapped(action), wrapped(contextSpec));
    }
    default void onDialogClosedMono(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onWrappedDialogClosedMono(wrapped(action), wrapped(contextSpec));
    }

    void onWrappedDialogOpenMono(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec);
    void onWrappedDialogMessageMono(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec);
    void onWrappedDialogMessageStateMono(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec);
    void onWrappedDialogCounterpartChangedMono(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec);
    void onWrappedDialogClosedMono(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec);

    void assertSubscribed();
}
