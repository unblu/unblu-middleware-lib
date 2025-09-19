package com.unblu.middleware.webhooks.controller;

import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.webhooks.service.WebhookRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.common.request.RequestHandler.withRequestContext;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "${unblu.webhook.api-path}", method = RequestMethod.POST)
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    @Qualifier("webhooksRequestHandler")
    private final RequestHandler requestHandler;
    private final WebhookRequestHandler webhookRequestHandler;

    @PostMapping
    public Mono<ResponseEntity<Object>> webhook(@RequestHeader("x-unblu-event") String eventType, ServerHttpRequest request) {

        if ("ping".equals(eventType)) {
            return Mono.just(ok("Pong!"));
        }

        return requestHandler.handle(request,
                body -> {
                    log.debug(withRequestContext("Start Processed webhook event: {}", request), eventType);
                    webhookRequestHandler.handle(eventName(eventType), body, request.getHeaders());
                    log.debug(withRequestContext("Processed webhook event: {}", request), eventType);
                    return Mono.just(ok(withRequestContext("Webhook processed", request)));
                });
    }
}
