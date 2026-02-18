package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

/**
 * CDI producer for configuration beans that converts Quarkus config mappings
 * to the core library's configuration POJOs.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    @ApplicationScoped
    public BotConfiguration botConfiguration() {
        return binder.bindPrefix(config, "unblu.bot", BotConfiguration.class);
    }

    @ApplicationScoped
    public MiddlewareConfiguration middlewareConfiguration() {
        return binder.bindPrefix(config, "unblu.middleware", MiddlewareConfiguration.class);
    }

    @ApplicationScoped
    public WebhookConfiguration webhookConfiguration() {
        return binder.bindPrefix(config, "unblu.webhook", WebhookConfiguration.class);
    }

    @ApplicationScoped
    public OutboundRequestsConfiguration outboundRequestsConfiguration() {
        return binder.bindPrefix(config, "unblu.outbound-requests", OutboundRequestsConfiguration.class);
    }

    @ApplicationScoped
    public ExternalMessengerConfiguration externalMessengerConfiguration() {
        return binder.bindPrefix(config, "unblu.external-messenger", ExternalMessengerConfiguration.class);
    }
}

