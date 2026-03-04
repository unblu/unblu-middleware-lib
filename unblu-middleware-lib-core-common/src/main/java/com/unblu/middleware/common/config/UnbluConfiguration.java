package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;

public record UnbluConfiguration(
        @NotBlank String host,
        @NotBlank String apiBasePath,
        @NotBlank String user,
        @NotBlank String password,
        String idPropagationHeaderName,
        String idPropagationUserId
) {
}
