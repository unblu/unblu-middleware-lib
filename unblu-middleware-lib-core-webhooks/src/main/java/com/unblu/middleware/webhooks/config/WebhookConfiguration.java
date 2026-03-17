package com.unblu.middleware.webhooks.config;

import com.unblu.middleware.webhooks.entity.EventName;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record WebhookConfiguration(
        @NotBlank String apiPath,
        @NotBlank String secret,
        Boolean cleanPrevious,
        Set<EventName> eventNames
) {
    public WebhookConfiguration {
        apiPath = apiPath == null || apiPath.isBlank() ? "/webhook" : apiPath;
        cleanPrevious = cleanPrevious == null ? Boolean.FALSE : cleanPrevious;
    }
}
