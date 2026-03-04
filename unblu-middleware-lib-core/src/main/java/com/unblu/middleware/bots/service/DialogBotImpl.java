package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.handler.OutboundHandler;
import com.unblu.webapi.model.v4.*;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;
import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Slf4j
@Named("DialogBot")
@Singleton
public class DialogBotImpl implements DialogBot {

    private final OutboundHandler outboundRequestHandler;

    public DialogBotImpl(OutboundHandler outboundRequestHandler) {
        this.outboundRequestHandler = outboundRequestHandler;
        acceptWrappedOnboardingOfferIfMono(_request -> Mono.just(false));
        acceptWrappedOffboardingOfferIfMono(_request -> Mono.just(false));
        acceptWrappedReboardingOfferIfMono(_request -> Mono.just(false));
        onWrappedDialogOpenMono(null, ContextSpec.empty());
        onWrappedDialogMessageMono(null, ContextSpec.empty());
        onWrappedDialogMessageStateMono(null, ContextSpec.empty());
        onWrappedDialogCounterpartChangedMono(null, ContextSpec.empty());
        onWrappedDialogClosedMono(null, ContextSpec.empty());
    }

    @Override
    public void acceptOnboardingOfferIfMono(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOnboardingOfferRequest> contextSpec) {
        acceptWrappedOnboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }

    @Override
    public void acceptReboardingOfferIfMono(Function<BotReboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotReboardingOfferRequest> contextSpec) {
        acceptWrappedReboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }

    @Override
    public void acceptOffboardingOfferIfMono(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition, ContextSpec<BotOffboardingOfferRequest> contextSpec) {
        acceptWrappedOffboardingOfferIfMono(wrapped(condition), wrapped(contextSpec));
    }

    @Override
    public void acceptWrappedOnboardingOfferIfMono(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        acceptWrappedOnboardingOfferIf(condition, contextSpec);
    }

    @Override
    public void acceptWrappedReboardingOfferIfMono(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        acceptWrappedReboardingOfferIf(condition, contextSpec);
    }

    @Override
    public void acceptWrappedOffboardingOfferIfMono(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        acceptWrappedOffboardingOfferIf(condition, contextSpec);
    }

    @Override
    public void onDialogOpenMono(Function<BotDialogOpenRequest, Mono<Void>> action, ContextSpec<BotDialogOpenRequest> contextSpec) {
        onWrappedDialogOpenMono(wrapped(action), wrapped(contextSpec));
    }

    @Override
    public void onDialogMessageMono(Function<BotDialogMessageRequest, Mono<Void>> action, ContextSpec<BotDialogMessageRequest> contextSpec) {
        onWrappedDialogMessageMono(wrapped(action), wrapped(contextSpec));
    }

    @Override
    public void onDialogMessageStateMono(Function<BotDialogMessageStateRequest, Mono<Void>> action, ContextSpec<BotDialogMessageStateRequest> contextSpec) {
        onWrappedDialogMessageStateMono(wrapped(action), wrapped(contextSpec));
    }

    @Override
    public void onDialogCounterpartChangedMono(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, ContextSpec<BotDialogCounterpartChangedRequest> contextSpec) {
        onWrappedDialogCounterpartChangedMono(wrapped(action), wrapped(contextSpec));
    }

    @Override
    public void onDialogClosedMono(Function<BotDialogClosedRequest, Mono<Void>> action, ContextSpec<BotDialogClosedRequest> contextSpec) {
        onWrappedDialogClosedMono(wrapped(action), wrapped(contextSpec));
    }

    @Override
    public void onWrappedDialogOpenMono(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        onWrappedDialogOpen(action, contextSpec);
    }

    @Override
    public void onWrappedDialogMessageMono(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        onWrappedDialogMessage(action, contextSpec);
    }

    @Override
    public void onWrappedDialogMessageStateMono(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        onWrappedDialogMessageState(action, contextSpec);
    }

    @Override
    public void onWrappedDialogCounterpartChangedMono(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        onWrappedDialogCounterpartChanged(action, contextSpec);
    }

    @Override
    public void onWrappedDialogClosedMono(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        onWrappedDialogClosed(action, contextSpec);
    }

    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOnboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.onboarding_offer"),
                BotOnboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting onboarding dialog offer" : "Rejecting onboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                OutboundRequestHandlerOptions.contextSpec(
                        contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                        ))));
    }

    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotReboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.reboarding_offer"),
                BotReboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting reboarding dialog offer" : "Rejecting reboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                OutboundRequestHandlerOptions.contextSpec(
                        contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                        ))));
    }

    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition, ContextSpec<Request<BotOffboardingOfferRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.offboarding_offer"),
                BotOffboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting offboarding dialog offer" : "Rejecting offboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                OutboundRequestHandlerOptions.contextSpec(
                        contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                        ))));
    }

    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogOpenRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.dialog.opened"),
                BotDialogOpenRequest.class,
                BotDialogOpenResponse.class,
                _request -> Mono.just(new BotDialogOpenResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog open")),
                action,
                OutboundRequestHandlerOptions.<Request<BotDialogOpenRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()))
                        .withContextSpec(contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> Optional.ofNullable(it.body().getConversation()).map(ConversationData::getId).orElse(null)
                        ))));
    }

    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.dialog.message"),
                BotDialogMessageRequest.class,
                BotDialogMessageResponse.class,
                _request -> Mono.just(new BotDialogMessageResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message")),
                action,
                OutboundRequestHandlerOptions.<Request<BotDialogMessageRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()))
                        .withContextSpec(contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> it.body().getConversationId()
                        ))));
    }

    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogMessageStateRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.dialog.message_state"),
                BotDialogMessageStateRequest.class,
                BotDialogMessageStateResponse.class,
                _request -> Mono.just(new BotDialogMessageStateResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message state")),
                action,
                OutboundRequestHandlerOptions.<Request<BotDialogMessageStateRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()))
                        .withContextSpec(contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> it.body().getConversationId()
                        ))));
    }

    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogCounterpartChangedRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.dialog.counterpart_changed"),
                BotDialogCounterpartChangedRequest.class,
                BotDialogCounterpartChangedResponse.class,
                _request -> Mono.just(new BotDialogCounterpartChangedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog counterpart changed")),
                action,
                OutboundRequestHandlerOptions.<Request<BotDialogCounterpartChangedRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()))
                        .withContextSpec(contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> it.body().getConversationId()
                        ))));
    }

    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, ContextSpec<Request<BotDialogClosedRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.bot.dialog.closed"),
                BotDialogClosedRequest.class,
                BotDialogClosedResponse.class,
                _request -> Mono.just(new BotDialogClosedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog closed")),
                action,
                OutboundRequestHandlerOptions.<Request<BotDialogClosedRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()))
                        .withContextSpec(contextSpec.with(ContextSpec.of(
                                "dialogToken", it -> it.body().getDialogToken(),
                                "conversationId", it -> it.body().getConversationId()
                        ))));
    }

    @Override
    public void assertSubscribed() {
        outboundRequestHandler.assertSubscribed();
    }
}
