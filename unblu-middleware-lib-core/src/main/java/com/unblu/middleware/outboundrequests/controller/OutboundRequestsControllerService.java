package com.unblu.middleware.outboundrequests.controller;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Named
@Singleton
@Slf4j
public class OutboundRequestsControllerService {

    private final RequestHandler requestHandler;
    private final OutboundRequestHandler outboundRequestHandler;

    // explicit constructor because otherwise @Named is not applied
    public OutboundRequestsControllerService(
            @Named("outboundRequestsRequestHandler") RequestHandler requestHandler,
            OutboundRequestHandler outboundRequestHandler) {
        this.requestHandler = requestHandler;
        this.outboundRequestHandler = outboundRequestHandler;
    }

    public Mono<HttpResponse<String>> outbound(String xUnbluServiceName, RawRequest request) {
        return requestHandler.handle(request, r -> {
            log.debug("Started processing outbound request: {}", xUnbluServiceName);
            return outboundRequestHandler.handle(outboundRequestType(xUnbluServiceName), r.body(), r)
                    .doOnNext(_r -> log.debug("Responded to outbound request: {}", xUnbluServiceName));
        });
    }
}
