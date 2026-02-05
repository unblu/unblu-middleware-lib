package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MiddlewareConfiguration {
    @NotBlank
    private String name;
    private String description = "";
    @NotBlank
    private String url;
    private boolean autoRegister = true;
    private boolean autoSubscribe = true;
    private boolean selfHealingEnabled = true;
    private long selfHealingCheckIntervalInSeconds = 60;
    private boolean pingUnbluOnStartup = true;
}
