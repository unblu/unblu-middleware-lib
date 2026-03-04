package com.unblu.middleware.outboundrequests.config;

import jakarta.validation.constraints.NotBlank;

public record OutboundRequestsConfiguration(
        @NotBlank String secret,
        @NotBlank String apiPath
) {
    public OutboundRequestsConfiguration {
        apiPath = apiPath == null || apiPath.isBlank() ? "/outbound" : apiPath;
    }
}
