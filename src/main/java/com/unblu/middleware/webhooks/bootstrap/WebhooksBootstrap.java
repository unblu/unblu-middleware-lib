package com.unblu.middleware.webhooks.bootstrap;

import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;

import static com.unblu.middleware.webhooks.util.WebhookContextSpecUtil.webhookHeadersContextSpec;

@Configuration
@RequiredArgsConstructor
public class WebhooksBootstrap {

    @Bean
    @Qualifier("webhooksRequestHandler")
    public RequestHandler webhooksRequestHandler(DataBufferFactory dataBufferFactory, WebhookConfiguration webhookConfiguration, ContextRegistryWrapper contextRegistryWrapper) {
        return new RequestHandler(dataBufferFactory, new RequestHandlerConfiguration(webhookConfiguration.getSecret()), contextRegistryWrapper, webhookHeadersContextSpec());
    }
}