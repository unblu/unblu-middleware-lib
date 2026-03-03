package com.unblu.middleware.core;

import com.unblu.middleware.bots.service.DialogBotServiceImpl;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.DefaultRequestQueueErrorHandler;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestType;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestQueue;
import com.unblu.webapi.model.v4.BotBoardingOfferResponse;
import com.unblu.webapi.model.v4.BotOffboardingOfferRequest;
import com.unblu.webapi.model.v4.BotOnboardingOfferRequest;
import com.unblu.webapi.model.v4.BotReboardingOfferRequest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpHeaders;
import java.time.Duration;
import java.util.Map;

import static com.unblu.middleware.common.utils.ObjectUtils.getObjectMapper;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogBotServiceOfferCoreTest {

    @Test
    void onboardingOfferIsAcceptedOnlyForConfiguredAccount() {
        var outboundRequestHandler = newOutboundRequestHandler();
        var dialogBotService = new DialogBotServiceImpl(outboundRequestHandler);

        dialogBotService.acceptOnboardingOfferIf(o -> reactor.core.publisher.Mono.just("testOnAccountId".equals(o.getAccountId())));
        outboundRequestHandler.assertSubscribed();

        var accepted = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.onboarding_offer"),
                new BotOnboardingOfferRequest().accountId("testOnAccountId"));
        var rejected = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.onboarding_offer"),
                new BotOnboardingOfferRequest().accountId("other"));

        assertTrue(accepted.isOfferAccepted());
        assertFalse(rejected.isOfferAccepted());
    }

    @Test
    void reboardingAndOffboardingOffersAreAcceptedOnlyForConfiguredAccounts() {
        var outboundRequestHandler = newOutboundRequestHandler();
        var dialogBotService = new DialogBotServiceImpl(outboundRequestHandler);

        dialogBotService.acceptReboardingOfferIf(o -> reactor.core.publisher.Mono.just("testReAccountId".equals(o.getAccountId())));
        dialogBotService.acceptOffboardingOfferIf(o -> reactor.core.publisher.Mono.just("testOffAccountId".equals(o.getAccountId())));
        outboundRequestHandler.assertSubscribed();

        var reAccepted = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.reboarding_offer"),
                new BotReboardingOfferRequest().accountId("testReAccountId"));
        var reRejected = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.reboarding_offer"),
                new BotReboardingOfferRequest().accountId("other"));

        var offAccepted = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.offboarding_offer"),
                new BotOffboardingOfferRequest().accountId("testOffAccountId"));
        var offRejected = invokeOffer(outboundRequestHandler,
                outboundRequestType("outbound.bot.offboarding_offer"),
                new BotOffboardingOfferRequest().accountId("other"));

        assertTrue(reAccepted.isOfferAccepted());
        assertFalse(reRejected.isOfferAccepted());
        assertTrue(offAccepted.isOfferAccepted());
        assertFalse(offRejected.isOfferAccepted());
    }

    private static BotBoardingOfferResponse invokeOffer(OutboundRequestHandler outboundRequestHandler, OutboundRequestType type, Object requestBody) {
        var response = outboundRequestHandler.handle(type, new Request<>(requestBody, emptyHeaders()))
                .block(Duration.ofSeconds(2));
        return (BotBoardingOfferResponse) response;
    }

    private static OutboundRequestHandler newOutboundRequestHandler() {
        var contextRegistryWrapper = new ContextRegistryWrapper();
        var outboundRequestQueue = new OutboundRequestQueue(
                () -> {
                },
                contextRegistryWrapper,
                new DefaultRequestQueueErrorHandler()
        );
        return new OutboundRequestHandler(outboundRequestQueue, getObjectMapper().copy(), contextRegistryWrapper);
    }

    private static HttpHeaders emptyHeaders() {
        return HttpHeaders.of(Map.of(), (_k, _v) -> true);
    }
}
