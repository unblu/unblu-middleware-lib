package com.unblu.middleware.common.config;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "unblu")
public class MiddlewareLibConfigurationProperties {
    private MiddlewareConfiguration middleware;
    private BotConfiguration bot;
    private WebhookConfiguration webhook;
    private OutboundRequestsConfiguration outboundRequests;
    private ExternalMessengerConfiguration externalMessenger;

    // must unwrap & copy for compatibility :-((
    @NotBlank
    private String host;
    @NotBlank
    private String apiBasePath;
    @NotBlank
    private String user;
    @NotBlank
    private String password;
    private String idPropagationHeaderName;
    private String idPropagationUserId;

    @Bean
    public MiddlewareConfiguration middlewareConfiguration() {
        return middleware;
    }

    @Bean
    public UnbluConfiguration unbluConfiguration() {
        return new UnbluConfiguration(host, apiBasePath, user, password, idPropagationHeaderName, idPropagationUserId);
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
