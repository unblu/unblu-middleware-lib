package com.unblu.middleware.quarkus.webhooks;

import com.unblu.middleware.config.DtoBinder;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class WebhookConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    public WebhookConfigurationProducer(SmallRyeConfig config, DtoBinder binder) {
        this.config = config;
        this.binder = binder;
    }

    @Produces
    @Singleton
    public WebhookConfiguration webhookConfiguration() {
        return binder.bindPrefix(config, "unblu.webhook", WebhookConfiguration.class);
    }
}
