package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.webapi.model.v4.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;

@Service
@Slf4j
public class DialogBotServiceImpl extends RequestQueueServiceImpl implements DialogBotService, BotsOutboundRequestHandler {

    private Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> onboardingOfferAcceptCondition = _r -> Mono.just(false);
    private Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> reboardingOfferAcceptCondition = _r -> Mono.just(false);
    private Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> offboardingOfferAcceptCondition = _r -> Mono.just(false);

    public DialogBotServiceImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    @Override
    public <T> void handle(Request<T> request) {
        requestQueue.queueRequest(request);
    }

    @Override
    public Mono<Boolean> shouldOnboard(Request<BotOnboardingOfferRequest> request) {
        return onboardingOfferAcceptCondition.apply(request);
    }

    @Override
    public Mono<Boolean> shouldReboard(Request<BotReboardingOfferRequest> request) {
        return reboardingOfferAcceptCondition.apply(request);
    }

    @Override
    public Mono<Boolean> shouldOffboard(Request<BotOffboardingOfferRequest> request) {
        return offboardingOfferAcceptCondition.apply(request);
    }

    @Override
    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        onboardingOfferAcceptCondition = condition;
    }

    @Override
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        reboardingOfferAcceptCondition = condition;
    }

    @Override
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        offboardingOfferAcceptCondition = condition;
    }

    @Override
    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogOpenRequest>>> contextEntries) {
        requestQueue.onWrapped(BotDialogOpenRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()), contextEntries);
    }

    @Override
    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageRequest>>> contextEntries) {
        requestQueue.onWrapped(BotDialogMessageRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()), contextEntries);
    }

    @Override
    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageStateRequest>>> contextEntries) {
        requestQueue.onWrapped(BotDialogMessageStateRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()), contextEntries);
    }

    @Override
    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogCounterpartChangedRequest>>> contextEntries) {
        requestQueue.onWrapped(BotDialogCounterpartChangedRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()), contextEntries);
    }

    @Override
    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogClosedRequest>>> contextEntries) {
        requestQueue.onWrapped(BotDialogClosedRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()), contextEntries);
    }
}
