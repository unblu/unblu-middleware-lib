package com.unblu.middleware.outboundrequests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.common.request.RequestHandler.withRequestContext;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "${unblu.outbound-requests.api-path}", method = RequestMethod.POST)
@Slf4j
public class OutboundRequestsController {

    @Qualifier("outboundRequestsRequestHandler")
    private final RequestHandler requestHandler;
    private final ObjectMapper objectMapper;
    private final OutboundRequestHandler outboundRequestHandler;
    private final HmacUtils hmacUtils;

    public OutboundRequestsController(@Qualifier("outboundRequestsRequestHandler") RequestHandler requestHandler, OutboundRequestsConfiguration outboundRequestsConfiguration, ObjectMapper objectMapper, OutboundRequestHandler outboundRequestHandler) {
        this.requestHandler = requestHandler;
        this.objectMapper = objectMapper;
        this.outboundRequestHandler = outboundRequestHandler;
        this.hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, outboundRequestsConfiguration.getSecret());
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> bots(@RequestHeader("x-unblu-service-name") String requestType, ServerHttpRequest request) {

        if ("outbound.ping".equals(requestType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request, body -> {
            log.debug(withRequestContext("Started processing outbound request: {}", request), requestType);
            return outboundRequestHandler.handle(outboundRequestType(requestType), body, request)
                    .map(this::signed);
        });
    }

    private ResponseEntity<Object> signed(Object body) {
        try {
            var bodySerialized = objectMapper.writeValueAsString(body);
            return ok()
                    .header("x-unblu-signature-256", hmacUtils.hmacHex(bodySerialized))
                    .contentType(APPLICATION_JSON)
                    .body(bodySerialized);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body", e);
            return internalServerError().body("Failed to serialize response body: " + e.getMessage());
        }
    }
}
