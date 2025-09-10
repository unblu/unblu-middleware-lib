package com.unblu.middleware.webhooks.config;

import com.unblu.middleware.webhooks.entity.EventName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "unblu.webhook")
@RequiredArgsConstructor
public class WebhookRegistrationConfiguration {
    @NotBlank
    private String secret;
    private boolean cleanPrevious = false;
    private Set<EventName> eventNames;
}
