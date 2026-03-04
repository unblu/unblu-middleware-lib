package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;

public record MiddlewareConfiguration(
        @NotBlank String name,
        String description,
        @NotBlank String url,
        Boolean autoRegister,
        Boolean autoSubscribe,
        Boolean selfHealingEnabled,
        Long selfHealingCheckIntervalInSeconds,
        Boolean pingUnbluOnStartup
) {
    public MiddlewareConfiguration {
        description = description == null ? "" : description;
        autoRegister = autoRegister == null ? Boolean.TRUE : autoRegister;
        autoSubscribe = autoSubscribe == null ? Boolean.TRUE : autoSubscribe;
        selfHealingEnabled = selfHealingEnabled == null ? Boolean.TRUE : selfHealingEnabled;
        selfHealingCheckIntervalInSeconds = selfHealingCheckIntervalInSeconds == null ? 60L : selfHealingCheckIntervalInSeconds;
        pingUnbluOnStartup = pingUnbluOnStartup == null ? Boolean.TRUE : pingUnbluOnStartup;
    }
}
