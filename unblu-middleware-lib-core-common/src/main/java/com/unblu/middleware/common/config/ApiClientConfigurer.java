package com.unblu.middleware.common.config;

import com.unblu.webapi.jersey.v4.invoker.ApiClient;

public final class ApiClientConfigurer {

    private ApiClientConfigurer() {
    }

    public static void configure(ApiClient apiClient, UnbluConfiguration configuration) {
        apiClient.setBasePath(configuration.host() + configuration.apiBasePath());

        if (configuration.bearerToken() != null && !configuration.bearerToken().isBlank()) {
            apiClient.setBearerToken(configuration.bearerToken());
        } else {
            apiClient.setUsername(configuration.user());
            apiClient.setPassword(configuration.password());
        }

        if (configuration.idPropagationHeaderName() != null && configuration.idPropagationUserId() != null) {
            apiClient.addDefaultHeader(configuration.idPropagationHeaderName(), configuration.idPropagationUserId());
        }
    }
}
