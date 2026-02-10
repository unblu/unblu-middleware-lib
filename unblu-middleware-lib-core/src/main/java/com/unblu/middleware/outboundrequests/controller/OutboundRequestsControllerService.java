package com.unblu.middleware.outboundrequests.controller;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestsRequestHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Named
@Singleton
@Slf4j
@RequiredArgsConstructor
public class OutboundRequestsControllerService {

    private final OutboundRequestsRequestHandler requestHandler;
    private final OutboundRequestHandler outboundRequestHandler;

    public Mono<HttpResponse<String>> outbound(String xUnbluServiceName, RawRequest request) {
        return requestHandler.handle(request, r -> {
            log.debug("Started processing outbound request: {}", xUnbluServiceName);
            return outboundRequestHandler.handle(outboundRequestType(xUnbluServiceName), r.body(), r)
                    .doOnNext(_r -> log.debug("Responded to outbound request: {}", xUnbluServiceName));
        });
    }
}
