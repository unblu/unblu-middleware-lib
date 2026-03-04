package com.unblu.middleware.config;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.config.UnbluConfiguration;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
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

    @Produces
    @Singleton
    public UnbluConfiguration unbluConfiguration() {
        return binder.bindPrefix(config, "unblu", UnbluConfiguration.class);
    }

    @Produces
    @Singleton
    public BotConfiguration botConfiguration() {
        return binder.bindPrefix(config, "unblu.bot", BotConfiguration.class);
    }

    @Produces
    @Singleton
    public MiddlewareConfiguration middlewareConfiguration() {
        return binder.bindPrefix(config, "unblu.middleware", MiddlewareConfiguration.class);
    }

    @Produces
    @Singleton
    public WebhookConfiguration webhookConfiguration() {
        return binder.bindPrefix(config, "unblu.webhook", WebhookConfiguration.class);
    }

    @Produces
    @Singleton
    public OutboundRequestsConfiguration outboundRequestsConfiguration() {
        return binder.bindPrefix(config, "unblu.outbound-requests", OutboundRequestsConfiguration.class);
    }

    @Produces
    @Singleton
    public ExternalMessengerConfiguration externalMessengerConfiguration() {
        return binder.bindPrefix(config, "unblu.external-messenger", ExternalMessengerConfiguration.class);
    }
}
