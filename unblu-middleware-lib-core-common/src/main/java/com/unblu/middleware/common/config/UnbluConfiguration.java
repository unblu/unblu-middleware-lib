package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;

public record UnbluConfiguration(
        @NotBlank String host,
        @NotBlank String apiBasePath,
        String user,
        String password,
        String bearerToken,
        String idPropagationHeaderName,
        String idPropagationUserId
) {
    public UnbluConfiguration {
        boolean hasBasicAuth = isNotBlank(user) && isNotBlank(password);
        boolean hasBearerToken = isNotBlank(bearerToken);
        if (hasBasicAuth == hasBearerToken) {
            throw new IllegalArgumentException(
                    "Exactly one Unblu API authentication method must be configured: " +
                            "either basic auth (unblu.user + unblu.password) or bearer token (unblu.bearer-token).");
        }
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
