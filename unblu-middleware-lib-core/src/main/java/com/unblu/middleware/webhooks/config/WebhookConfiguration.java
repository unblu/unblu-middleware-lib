package com.unblu.middleware.webhooks.config;

import com.unblu.middleware.webhooks.entity.EventName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class WebhookConfiguration {
    @NotBlank
    private String apiPath = "/webhook";
    @NotBlank
    private String secret;
    private boolean cleanPrevious = false;
    private Set<EventName> eventNames;
}
