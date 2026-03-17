package com.unblu.middleware.webhooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.request.RequestHandler;
import com.unblu.middleware.common.request.RequestHandlerConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import static com.unblu.middleware.webhooks.util.WebhookContextSpecUtil.webhookHeadersContextSpec;

@Named
@Singleton
public class WebhooksRequestHandler extends RequestHandler {
    public WebhooksRequestHandler(WebhookConfiguration webhookConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper) {
        super(new RequestHandlerConfiguration(webhookConfiguration.secret()), contextRegistryWrapper, objectMapper, webhookHeadersContextSpec());
    }
}
