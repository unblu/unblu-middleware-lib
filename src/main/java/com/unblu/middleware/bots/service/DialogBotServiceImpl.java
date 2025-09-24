package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.webapi.model.v4.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Service
@Slf4j
public class DialogBotServiceImpl implements DialogBotService {

    private final OutboundRequestHandler outboundRequestHandler;

    public DialogBotServiceImpl(OutboundRequestHandler outboundRequestHandler) {
        this.outboundRequestHandler = outboundRequestHandler;
        acceptWrappedOnboardingOfferIf(_request -> Mono.just(false));
        acceptWrappedOffboardingOfferIf(_request -> Mono.just(false));
        acceptWrappedReboardingOfferIf(_request -> Mono.just(false));
        onWrappedDialogOpen(null, ContextSpec.empty());
        onWrappedDialogMessage(null, ContextSpec.empty());
        onWrappedDialogMessageState(null, ContextSpec.empty());
        onWrappedDialogCounterpartChanged(null, ContextSpec.empty());
        onWrappedDialogClosed(null, ContextSpec.empty());
    }

    @Override
    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.onboarding_offer"),
                BotOnboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting onboarding dialog offer" : "Rejecting onboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                )));
    }

    @Override
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.reboarding_offer"),
                BotReboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting reboarding dialog offer" : "Rejecting reboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                )));
    }

    @Override
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.offboarding_offer"),
                BotOffboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting offboarding dialog offer" : "Rejecting offboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                )));
    }

    @Override
    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.opened"),
                BotDialogOpenRequest.class,
                BotDialogOpenResponse.class,
                _request -> Mono.just(new BotDialogOpenResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog open")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                )));
    }

    @Override
    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.message"),
                BotDialogMessageRequest.class,
                BotDialogMessageResponse.class,
                _request -> Mono.just(new BotDialogMessageResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> it.body().getConversationId()
                )));
    }

    @Override
    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.message_state"),
                BotDialogMessageStateRequest.class,
                BotDialogMessageStateResponse.class,
                _request -> Mono.just(new BotDialogMessageStateResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message state")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> it.body().getConversationId()
                )));
    }

    @Override
    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.counterpart_changed"),
                BotDialogCounterpartChangedRequest.class,
                BotDialogCounterpartChangedResponse.class,
                _request -> Mono.just(new BotDialogCounterpartChangedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog counterpart changed")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> it.body().getConversationId()
                )));
    }

    @Override
    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.closed"),
                BotDialogClosedRequest.class,
                BotDialogClosedResponse.class,
                _request -> Mono.just(new BotDialogClosedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog closed")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextSpec.with(ContextSpec.of(
                        "dialogToken", it -> it.body().getDialogToken(),
                        "conversationId", it -> it.body().getConversationId()
                )));
    }

    @Override
    public void assertSubscribed() {
        outboundRequestHandler.assertSubscribed();
    }
}
