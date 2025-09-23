package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.webapi.model.v4.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
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
        onWrappedDialogOpen(null, List.of());
        onWrappedDialogMessage(null, List.of());
        onWrappedDialogMessageState(null, List.of());
        onWrappedDialogCounterpartChanged(null, List.of());
        onWrappedDialogClosed(null, List.of());
    }

    @Override
    public void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.onboarding_offer"),
                BotOnboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting onboarding dialog offer" : "Rejecting onboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                List.of());
    }

    @Override
    public void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.reboarding_offer"),
                BotReboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting reboarding dialog offer" : "Rejecting reboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                List.of());
    }

    @Override
    public void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.offboarding_offer"),
                BotOffboardingOfferRequest.class,
                BotBoardingOfferResponse.class,
                request -> condition.apply(request)
                        .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting offboarding dialog offer" : "Rejecting offboarding dialog offer"))
                        .map(shouldAccept -> new BotBoardingOfferResponse().offerAccepted(shouldAccept)),
                null,
                null,
                List.of());
    }

    @Override
    public void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogOpenRequest>>> contextEntries) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.opened"),
                BotDialogOpenRequest.class,
                BotDialogOpenResponse.class,
                _request -> Mono.just(new BotDialogOpenResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog open")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextEntries);
    }

    @Override
    public void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageRequest>>> contextEntries) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.message"),
                BotDialogMessageRequest.class,
                BotDialogMessageResponse.class,
                _request -> Mono.just(new BotDialogMessageResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextEntries);
    }

    @Override
    public void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageStateRequest>>> contextEntries) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.message_state"),
                BotDialogMessageStateRequest.class,
                BotDialogMessageStateResponse.class,
                _request -> Mono.just(new BotDialogMessageStateResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog message state")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextEntries);
    }

    @Override
    public void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogCounterpartChangedRequest>>> contextEntries) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.counterpart_changed"),
                BotDialogCounterpartChangedRequest.class,
                BotDialogCounterpartChangedResponse.class,
                _request -> Mono.just(new BotDialogCounterpartChangedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog counterpart changed")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextEntries);
    }

    @Override
    public void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogClosedRequest>>> contextEntries) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.bot.dialog.closed"),
                BotDialogClosedRequest.class,
                BotDialogClosedResponse.class,
                _request -> Mono.just(new BotDialogClosedResponse())
                        .doOnNext(_response -> log.debug("Responding to bot dialog closed")),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getDialogToken()),
                contextEntries);
    }

    @Override
    public void assertSubscribed() {
        outboundRequestHandler.assertSubscribed();
    }
}
