package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.BotOffboardingOfferRequest;
import com.unblu.webapi.model.v4.BotOnboardingOfferRequest;
import com.unblu.webapi.model.v4.BotReboardingOfferRequest;
import reactor.core.publisher.Mono;

public interface BotsOutboundRequestHandler {
    <T> void handle(Request<T> request);

    Mono<Boolean> shouldOnboard(Request<BotOnboardingOfferRequest> request);

    Mono<Boolean> shouldReboard(Request<BotReboardingOfferRequest> request);

    Mono<Boolean> shouldOffboard(Request<BotOffboardingOfferRequest> request);
}
