package com.unblu.middleware.bots.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.bots.service.BotsOutboundRequestHandler;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.webapi.model.v4.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static com.unblu.middleware.common.request.RequestHandler.withRequestContext;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/bot", method = RequestMethod.POST)
@RequiredArgsConstructor
@Slf4j
public class BotsController {

    @Qualifier("botsRequestHandler")
    private final RequestHandler requestHandler;
    private final BotConfiguration botConfiguration;
    private final ObjectMapper objectMapper;
    private final BotsOutboundRequestHandler botsOutboundRequestHandler;

    @PostMapping
    public Mono<ResponseEntity<Object>> bots(@RequestHeader("x-unblu-service-name") String requestType, ServerHttpRequest request) {

        if ("outbound.ping".equals(requestType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request, body ->
                switch (requestType) {
                    case "outbound.bot.onboarding_offer" -> handleOnboardingDialogOffer(body, request);
                    case "outbound.bot.reboarding_offer" -> handleReboardingDialogOffer(body, request);
                    case "outbound.bot.offboarding_offer" -> handleOffboardingDialogOffer(body, request);
                    case "outbound.bot.dialog.opened" -> handleDialogOpen(body, request);
                    case "outbound.bot.dialog.message" -> handleDialogMessage(body, request);
                    case "outbound.bot.dialog.message_state" -> handleDialogMessageState(body, request);
                    case "outbound.bot.dialog.counterpart_changed" -> handleDialogCounterpartChanged(body, request);
                    case "outbound.bot.dialog.closed" -> handleDialogClosed(body, request);
                    default -> Mono.just(badRequest().body("Unknown request type: " + requestType));
                });
    }

    private Mono<ResponseEntity<Object>> handleOnboardingDialogOffer(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Received onboarding offer", request));
        try {
            var offer = objectMapper.readValue(body, BotOnboardingOfferRequest.class);
            return botsOutboundRequestHandler.shouldOnboard(new Request<>(offer, request.getHeaders()))
                    .doOnNext(shouldAccept -> log.debug(withRequestContext(shouldAccept ? "Accepting onboarding offer" : "Rejecting onboarding offer", request)))
                    .map(shouldAccept -> signed(new BotBoardingOfferResponse().offerAccepted(shouldAccept)));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse onboarding offer request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleReboardingDialogOffer(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Received reboarding offer", request));
        try {
            var offer = objectMapper.readValue(body, BotReboardingOfferRequest.class);
            return botsOutboundRequestHandler.shouldReboard(new Request<>(offer, request.getHeaders()))
                    .doOnNext(shouldAccept -> log.debug(withRequestContext(shouldAccept ? "Accepting reboarding offer" : "Rejecting reboarding offer", request)))
                    .map(shouldAccept -> signed(new BotBoardingOfferResponse().offerAccepted(shouldAccept)));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse reboarding offer request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleOffboardingDialogOffer(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Received offboarding offer", request));
        try {
            var offer = objectMapper.readValue(body, BotOffboardingOfferRequest.class);
            return botsOutboundRequestHandler.shouldOffboard(new Request<>(offer, request.getHeaders()))
                    .doOnNext(shouldAccept -> log.debug(shouldAccept ? "Accepting offboarding offer" : "Rejecting offboarding offer", request))
                    .map(shouldAccept -> signed(new BotBoardingOfferResponse().offerAccepted(shouldAccept)));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse offboarding offer request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleDialogOpen(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Dialog open request", request));
        try {
            botsOutboundRequestHandler.handle(new Request<>(objectMapper.readValue(body, BotDialogOpenRequest.class), request.getHeaders()));
            return Mono.just(signed(new BotDialogOpenResponse()));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse dialog open request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleDialogMessage(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Dialog message", request));
        try {
            botsOutboundRequestHandler.handle(new Request<>(objectMapper.readValue(body, BotDialogMessageRequest.class), request.getHeaders()));
            return Mono.just(signed(new BotDialogMessageResponse()));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse dialog message request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleDialogMessageState(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Dialog message state", request));
        try {
            botsOutboundRequestHandler.handle(new Request<>(objectMapper.readValue(body, BotDialogMessageStateRequest.class), request.getHeaders()));
            return Mono.just(signed(new BotDialogMessageStateResponse()));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse dialog message state request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleDialogCounterpartChanged(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Dialog counterpart changed", request));
        try {
            botsOutboundRequestHandler.handle(new Request<>(objectMapper.readValue(body, BotDialogCounterpartChangedRequest.class), request.getHeaders()));
            return Mono.just(signed(new BotDialogCounterpartChangedResponse()));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse dialog counterpart changed request", request), e);
        }
    }

    private Mono<ResponseEntity<Object>> handleDialogClosed(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("Dialog closed", request));
        try {
            botsOutboundRequestHandler.handle(new Request<>(objectMapper.readValue(body, BotDialogClosedRequest.class), request.getHeaders()));
            return Mono.just(signed(new BotDialogClosedResponse()));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse dialog closed request", request), e);
        }
    }

    private ResponseEntity<Object> signed(Object body) {
        try {
            var bodySerialized = objectMapper.writeValueAsString(body);
            return ok()
                    .header("x-unblu-signature-256", new HmacUtils(HmacAlgorithms.HMAC_SHA_256, botConfiguration.getSecret()).hmacHex(bodySerialized))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(bodySerialized);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body", e);
            return internalServerError().body("Failed to serialize response body: " + e.getMessage());
        }
    }
}
