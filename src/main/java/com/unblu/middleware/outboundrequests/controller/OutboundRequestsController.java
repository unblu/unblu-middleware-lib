package com.unblu.middleware.outboundrequests.controller;

import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "${unblu.outbound-requests.api-path}", method = RequestMethod.POST)
@RequiredArgsConstructor
@Slf4j
public class OutboundRequestsController {

    @Qualifier("outboundRequestsRequestHandler")
    private final RequestHandler requestHandler;
    private final OutboundRequestHandler outboundRequestHandler;

    @PostMapping
    public Mono<ResponseEntity<String>> outbound(@RequestHeader("x-unblu-service-name") String requestType, ServerHttpRequest request) {

        if ("outbound.ping".equals(requestType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request, body -> {
            log.debug("Started processing outbound request: {}", requestType);
            return outboundRequestHandler.handle(outboundRequestType(requestType), body, request)
                    .doOnNext(_r -> log.debug("Processed outbound request: {}", requestType));
        });
    }
}
