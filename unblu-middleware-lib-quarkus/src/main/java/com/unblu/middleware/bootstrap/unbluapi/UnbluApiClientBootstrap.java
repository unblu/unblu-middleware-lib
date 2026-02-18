package com.unblu.middleware.bootstrap.unbluapi;

import com.unblu.middleware.common.config.UnbluConfiguration;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class UnbluApiClientBootstrap {

    @Inject
    UnbluConfiguration configuration;

    @Produces
    @ApplicationScoped
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(configuration.getHost() + configuration.getApiBasePath());
        apiClient.setUsername(configuration.getUser());
        apiClient.setPassword(configuration.getPassword());

        if (configuration.getIdPropagationHeaderName() != null && configuration.getIdPropagationUserId() != null) {
            apiClient.addDefaultHeader(configuration.getIdPropagationHeaderName(), configuration.getIdPropagationUserId());
        }

        return apiClient;
    }
}
