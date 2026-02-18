package com.unblu.middleware.bootstrap.unbluapi;

import com.unblu.webapi.jersey.v4.api.*;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * CDI producer for Unblu API client and all API instances.
 * All APIs are produced as @ApplicationScoped beans that can be injected.
 */
@ApplicationScoped
public class UnbluApis {

    @Produces
    @ApplicationScoped
    public BotsApi botsApi(ApiClient apiClient) {
        return new BotsApi(apiClient);
    }


    @Produces
    @ApplicationScoped
    public CustomActionsApi customActionsApi(ApiClient apiClient) {
        return new CustomActionsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public GlobalApi globalApi(ApiClient apiClient) {
        return new GlobalApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public ExternalMessengersApi externalMessengersApi(ApiClient apiClient) {
        return new ExternalMessengersApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public PersonsApi personsApi(ApiClient apiClient) {
        return new PersonsApi(apiClient);
    }

    @Produces
    @ApplicationScoped
    public WebhookRegistrationsApi webhookRegistrationsApi(ApiClient apiClient) {
        return new WebhookRegistrationsApi(apiClient);
    }
}

