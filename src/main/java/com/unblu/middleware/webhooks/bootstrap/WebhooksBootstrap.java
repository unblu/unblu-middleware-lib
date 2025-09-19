package com.unblu.middleware.webhooks.bootstrap;

import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;

@Configuration
@RequiredArgsConstructor
public class WebhooksBootstrap {

    @Bean
    @Qualifier("webhooksRequestHandler")
    public RequestHandler webhooksRequestHandler(DataBufferFactory dataBufferFactory, WebhookConfiguration webhookConfiguration) {
        return new RequestHandler(dataBufferFactory, new RequestHandlerConfiguration(webhookConfiguration.getSecret()));
    }
}