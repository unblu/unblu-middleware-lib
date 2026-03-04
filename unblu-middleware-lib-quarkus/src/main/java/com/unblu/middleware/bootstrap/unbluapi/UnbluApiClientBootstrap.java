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
        apiClient.setBasePath(configuration.host() + configuration.apiBasePath());
        apiClient.setUsername(configuration.user());
        apiClient.setPassword(configuration.password());

        if (configuration.idPropagationHeaderName() != null && configuration.idPropagationUserId() != null) {
            apiClient.addDefaultHeader(configuration.idPropagationHeaderName(), configuration.idPropagationUserId());
        }

        return apiClient;
    }
}
