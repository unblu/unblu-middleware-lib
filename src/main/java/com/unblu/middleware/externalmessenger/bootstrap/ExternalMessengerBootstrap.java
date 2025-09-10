package com.unblu.middleware.externalmessenger.bootstrap;

import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;

@Configuration
@RequiredArgsConstructor
public class ExternalMessengerBootstrap {

    @Bean
    @Qualifier("externalMessengerRequestHandler")
    public RequestHandler externalMessengerRequestHandler(DataBufferFactory dataBufferFactory, ExternalMessengerConfiguration externalMessengerConfiguration) {
        return new RequestHandler(dataBufferFactory, new RequestHandlerConfiguration(externalMessengerConfiguration.getSecret()));
    }
}