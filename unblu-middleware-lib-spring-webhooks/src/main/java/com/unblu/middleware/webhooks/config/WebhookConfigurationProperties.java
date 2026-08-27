package com.unblu.middleware.webhooks.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "unblu")
public class WebhookConfigurationProperties {

    @Valid
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
