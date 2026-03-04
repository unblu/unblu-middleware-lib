package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.handler.OutboundHandlerImpl;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.webapi.model.v4.*;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @deprecated Renamed to DialogBotImpl
 */
@Deprecated
@Named("DialogBotService")
@Singleton
public class DialogBotServiceImpl implements DialogBotService {

    private final DialogBot dialogBot;

    @Inject
    public DialogBotServiceImpl(DialogBot dialogBot) {
        this.dialogBot = dialogBot;
    }

    public DialogBotServiceImpl(OutboundRequestHandler outboundRequestHandler) {
        this(new DialogBotImpl(new OutboundHandlerImpl(outboundRequestHandler)));
    }

    @Override
    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedOnboardingOfferIfMono(condition, contextSpec);
    }

    @Override
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedReboardingOfferIfMono(condition, contextSpec);
    }

    @Override
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        dialogBot.acceptWrappedOffboardingOfferIfMono(condition, contextSpec);
    }

    @Override
    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        dialogBot.onWrappedDialogOpenMono(action, contextSpec);
    }

    @Override
    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        dialogBot.onWrappedDialogMessageMono(action, contextSpec);
    }

    @Override
    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        dialogBot.onWrappedDialogMessageStateMono(action, contextSpec);
    }

    @Override
    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        dialogBot.onWrappedDialogCounterpartChangedMono(action, contextSpec);
    }

    @Override
    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        dialogBot.onWrappedDialogClosedMono(action, contextSpec);
    }

    @Override
    public void assertSubscribed() {
        dialogBot.assertSubscribed();
    }
}
