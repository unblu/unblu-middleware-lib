package com.unblu.middleware.webhooks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "unblu")
public class WebhookConfigurationProperties {

    @NestedConfigurationProperty
    private WebhookConfiguration webhook;

    public WebhookConfiguration getWebhook() {
        return webhook;
    }

    public void setWebhook(WebhookConfiguration webhook) {
        this.webhook = webhook;
    }

    @Bean
    public WebhookConfiguration webhookConfiguration() {
        return webhook;
    }
}
