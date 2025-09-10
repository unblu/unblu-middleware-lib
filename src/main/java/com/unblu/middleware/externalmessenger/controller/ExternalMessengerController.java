package com.unblu.middleware.externalmessenger.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.externalmessenger.service.ExternalMessengerOutboundRequestHandler;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
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
@RequestMapping(value = "/external-messenger", method = RequestMethod.POST)
@RequiredArgsConstructor
@Slf4j
public class ExternalMessengerController {

    @Qualifier("externalMessengerRequestHandler")
    private final RequestHandler requestHandler;
    private final ExternalMessengerConfiguration externalMessengerConfiguration;
    private final ObjectMapper objectMapper;
    private final ExternalMessengerOutboundRequestHandler outboundRequestHandler;

    @PostMapping
    public Mono<ResponseEntity<Object>> externalMessenger(@RequestHeader("x-unblu-service-name") String requestType, ServerHttpRequest request) {

        if ("outbound.ping".equals(requestType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request, body ->
                switch (requestType) {
                    case "outbound.external_messenger.new_message" -> handleNewMessage(body, request);
                    default -> Mono.just(badRequest().body("Unknown request type: " + requestType));
                });
    }

    private Mono<ResponseEntity<Object>> handleNewMessage(byte[] body, ServerHttpRequest request) {
        log.debug(withRequestContext("New external message request", request));
        try {
            var requestBody = objectMapper.readValue(body, ExternalMessengerNewMessageRequest.class);
            outboundRequestHandler.handle(new Request<>(requestBody, request.getHeaders()));
            return Mono.just(signed(new ExternalMessengerNewMessageResponse().externalMessageId(requestBody.getConversationMessage().getExternalMessageId())));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse new external message request", request), e);
        }
    }

    private ResponseEntity<Object> signed(Object body) {
        try {
            var bodySerialized = objectMapper.writeValueAsString(body);
            return ok()
                    .header("x-unblu-signature-256", new HmacUtils(HmacAlgorithms.HMAC_SHA_256, externalMessengerConfiguration.getSecret()).hmacHex(bodySerialized))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(bodySerialized);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body", e);
            return internalServerError().body("Failed to serialize response body: " + e.getMessage());
        }
    }
}
