package com.unblu.middleware.common.config;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "unblu")
public class MiddlewareLibConfigurationProperties {
    private MiddlewareConfiguration middleware;
    private UnbluConfiguration server;
    private BotConfiguration bot;
    private WebhookConfiguration webhook;
    private OutboundRequestsConfiguration outboundRequests;
    private ExternalMessengerConfiguration externalMessenger;

    @Bean
    public MiddlewareConfiguration middlewareConfiguration() {
        return middleware;
    }

    @Bean
    public UnbluConfiguration unbluConfiguration() {
        return server;
    }

    @Bean
    public BotConfiguration botConfiguration() {
        return bot;
    }

    @Bean
    public WebhookConfiguration webhookConfiguration() {
        return webhook;
    }

    @Bean
    public OutboundRequestsConfiguration outboundRequestsConfiguration() {
        return outboundRequests;
    }

    @Bean
    public ExternalMessengerConfiguration externalMessengerConfiguration() {
        return externalMessenger;
    }
}
