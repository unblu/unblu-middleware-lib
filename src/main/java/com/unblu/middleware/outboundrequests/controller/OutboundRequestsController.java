package com.unblu.middleware.outboundrequests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
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

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "${unblu.outbound-requests.api-path}", method = RequestMethod.POST)
@RequiredArgsConstructor
@Slf4j
public class OutboundRequestsController {

    @Qualifier("outboundRequestsRequestHandler")
    private final RequestHandler requestHandler;
    private final OutboundRequestsConfiguration outboundRequestsConfiguration;
    private final ObjectMapper objectMapper;
    private final OutboundRequestHandler outboundRequestHandler;

    @PostMapping
    public Mono<ResponseEntity<Object>> bots(@RequestHeader("x-unblu-service-name") String requestType, ServerHttpRequest request) {

        if ("outbound.ping".equals(requestType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request, body ->
                outboundRequestHandler.handle(outboundRequestType(requestType), body, request)
                        .map(this::signed)
        );
    }

    private ResponseEntity<Object> signed(Object body) {
        try {
            var bodySerialized = objectMapper.writeValueAsString(body);
            return ok()
                    .header("x-unblu-signature-256", new HmacUtils(HmacAlgorithms.HMAC_SHA_256, outboundRequestsConfiguration.getSecret()).hmacHex(bodySerialized))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(bodySerialized);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body", e);
            return internalServerError().body("Failed to serialize response body: " + e.getMessage());
        }
    }
}
