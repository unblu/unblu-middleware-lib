package com.unblu.middleware.outboundrequests.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import static com.unblu.middleware.outboundrequests.util.OutboundRequestsContextSpecUtil.outboundRequestHeadersContextSpec;

@Named
@Singleton
public class OutboundRequestsRequestHandler extends RequestHandler {
    public OutboundRequestsRequestHandler(OutboundRequestsConfiguration outboundRequestsConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper) {
        super(new RequestHandlerConfiguration(outboundRequestsConfiguration.secret()), contextRegistryWrapper, objectMapper, outboundRequestHeadersContextSpec());
    }
}
