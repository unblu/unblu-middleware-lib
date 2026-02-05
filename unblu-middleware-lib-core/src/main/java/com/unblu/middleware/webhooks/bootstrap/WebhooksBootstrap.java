package com.unblu.middleware.webhooks.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueErrorHandler;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import static com.unblu.middleware.webhooks.util.WebhookContextSpecUtil.webhookHeadersContextSpec;

@RequiredArgsConstructor
public class WebhooksBootstrap {

    @Named("webhooksRequestHandler")
    @Singleton
    public RequestHandler webhooksRequestHandler(WebhookConfiguration webhookConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper) {
        return new RequestHandler(new RequestHandlerConfiguration(webhookConfiguration.getSecret()), contextRegistryWrapper, objectMapper, webhookHeadersContextSpec());
    }

    @Named("webhookRequestQueue")
    @Singleton
    public RequestQueue webhookRequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper, RequestQueueErrorHandler requestQueueErrorHandler) {
        return new RequestQueue(fatalStartupErrorHandler, contextRegistryWrapper, requestQueueErrorHandler);
    }
}