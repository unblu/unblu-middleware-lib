package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @deprecated Renamed to DialogBot, signatures changed to methodNameMono()
 */
@Deprecated
@Lazy
@Service
@RequiredArgsConstructor
public class DialogBotService {

    private final DialogBot dialogBot;

    public void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        dialogBot.acceptOnboardingOfferIfMono(condition);
    }
    public void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        dialogBot.acceptReboardingOfferIfMono(condition);
    }
    public void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        dialogBot.acceptOffboardingOfferIfMono(condition);
    }

    public void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        dialogBot.acceptOnboardingOfferIfMono(condition, contextSpec);
    }
    public void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        dialogBot.acceptReboardingOfferIfMono(condition, contextSpec);
    }
    public void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        dialogBot.acceptOffboardingOfferIfMono(condition, contextSpec);
    }

    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        dialogBot.acceptWrappedOnboardingOfferIfMono(condition);
    }
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        dialogBot.acceptWrappedReboardingOfferIfMono(condition);
    }
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        dialogBot.acceptWrappedOffboardingOfferIfMono(condition);
    }

    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedOnboardingOfferIfMono(condition, contextSpec);
    }
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedReboardingOfferIfMono(condition, contextSpec);
    }
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedOffboardingOfferIfMono(condition, contextSpec);
    }

    public void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action) {
        dialogBot.onDialogOpenMono(action);
    }
    public void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action) {
        dialogBot.onDialogMessageMono(action);
    }
    public void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action) {
        dialogBot.onDialogMessageStateMono(action);
    }
    public void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action) {
        dialogBot.onDialogCounterpartChangedMono(action);
    }
    public void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action) {
        dialogBot.onDialogClosedMono(action);
    }

    public void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        dialogBot.onDialogOpenMono(action, contextSpec);
    }
    public void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        dialogBot.onDialogMessageMono(action, contextSpec);
    }
    public void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        dialogBot.onDialogMessageStateMono(action, contextSpec);
    }
    public void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        dialogBot.onDialogCounterpartChangedMono(action, contextSpec);
    }
    public void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        dialogBot.onDialogClosedMono(action, contextSpec);
    }

    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        dialogBot.onWrappedDialogOpenMono(action, contextSpec);
    }
    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        dialogBot.onWrappedDialogMessageMono(action, contextSpec);
    }
    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        dialogBot.onWrappedDialogMessageStateMono(action, contextSpec);
    }
    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        dialogBot.onWrappedDialogCounterpartChangedMono(action, contextSpec);
    }
    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        dialogBot.onWrappedDialogClosedMono(action, contextSpec);
    }

    public void assertSubscribed() {
        dialogBot.assertSubscribed();
    }
}
