package com.unblu.middleware.outboundrequests.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueErrorHandler;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import static com.unblu.middleware.outboundrequests.util.OutboundRequestsContextSpecUtil.outboundRequestHeadersContextSpec;

@RequiredArgsConstructor
public class OutboundRequestsBootstrap {

    @Named("outboundRequestsRequestHandler")
    @Singleton
    public RequestHandler outboundRequestsRequestHandler(OutboundRequestsConfiguration outboundRequestsConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper) {
        return new RequestHandler(new RequestHandlerConfiguration(outboundRequestsConfiguration.getSecret()), contextRegistryWrapper, objectMapper, outboundRequestHeadersContextSpec());
    }

    @Named("outboundRequestQueue")
    @Singleton
    public RequestQueue outboundRequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper, RequestQueueErrorHandler requestQueueErrorHandler) {
        return new RequestQueue(fatalStartupErrorHandler, contextRegistryWrapper, requestQueueErrorHandler);
    }
}