package com.unblu.middleware.webhooks.controller;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.webhooks.service.WebhookRequestHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.webhooks.entity.EventName.eventName;

@Named
@Singleton
@Slf4j
public class WebhookControllerService {

    private final RequestHandler requestHandler;
    private final WebhookRequestHandler webhookRequestHandler;

    // explicit constructor because otherwise @Named is not applied
    public WebhookControllerService(
            @Named("webhooksRequestHandler") RequestHandler requestHandler,
            WebhookRequestHandler webhookRequestHandler) {
        this.requestHandler = requestHandler;
        this.webhookRequestHandler = webhookRequestHandler;
    }

    public Mono<HttpResponse<String>> webhook(String xUnbluEvent, RawRequest request) {
        return requestHandler.handle(request, r -> {
            log.debug("Start processing webhook event: {}", xUnbluEvent);
            webhookRequestHandler.handle(eventName(xUnbluEvent), r.body(), r.headers());
            return Mono.just(new WebhookResponse())
                    .doOnNext(_r -> log.debug("Responded to webhook event: {}", xUnbluEvent));
        });
    }
}
