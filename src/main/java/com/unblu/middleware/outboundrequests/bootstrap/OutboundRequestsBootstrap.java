package com.unblu.middleware.outboundrequests.bootstrap;

import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;

@Configuration
@RequiredArgsConstructor
public class OutboundRequestsBootstrap {

    @Bean
    @Qualifier("outboundRequestsRequestHandler")
    public RequestHandler outboundRequestsRequestHandler(DataBufferFactory dataBufferFactory, OutboundRequestsConfiguration outboundRequestsConfiguration) {
        return new RequestHandler(dataBufferFactory, new RequestHandlerConfiguration(outboundRequestsConfiguration.getSecret()));
    }
}