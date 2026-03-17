package com.unblu.middleware.webhooks;

import com.unblu.middleware.Utils;
import com.unblu.middleware.webhooks.controller.WebhookControllerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${unblu.webhook.api-path}", method = RequestMethod.POST)
public class WebhookController {

    private final WebhookControllerService webhookControllerService;

    @PostMapping
    public Mono<ResponseEntity<String>> webhook(
            @RequestHeader("x-unblu-event") String xUnbluEvent,
            @RequestBody Flux<DataBuffer> bodyBuffer,
            @RequestHeader HttpHeaders headers
    ) {

        return Utils.toLibHttpRequest(bodyBuffer, headers)
                .flatMap(rawRequest -> webhookControllerService.webhook(xUnbluEvent, rawRequest))
                .map(Utils::libHttpResponseToResponseEntity);
    }
}
