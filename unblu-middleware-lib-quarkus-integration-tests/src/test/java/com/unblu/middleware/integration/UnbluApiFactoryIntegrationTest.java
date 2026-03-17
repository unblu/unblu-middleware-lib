package com.unblu.middleware.integration;

import com.unblu.middleware.bootstrap.unbluapi.UnbluApiFactory;
import com.unblu.webapi.jersey.v4.api.AccountsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for UnbluApiFactory CDI integration.
 * Tests that the factory is properly injected in a Quarkus context.
 */
@QuarkusTest
class UnbluApiFactoryIntegrationTest {

    @Inject
    UnbluApiFactory unbluApiFactory;

    @Inject
    ApiClient apiClient;

    @Inject
    AccountsApi accountsApi;

    @Test
    void testAccountsApiIsInjected() {
        assertNotNull(accountsApi, "AccountsApi should be injected");
    }

    @Test
    void testFactoryIsInjected() {
        assertNotNull(unbluApiFactory, "UnbluApiFactory should be injected");
    }

    @Test
    void testApiClientIsInjected() {
        assertNotNull(apiClient, "ApiClient should be injected");
        assertNotNull(apiClient.getBasePath(), "ApiClient should have base path configured");
        assertTrue(apiClient.getBasePath().contains("localhost"), "ApiClient should use test configuration");
    }

    @Test
    void testFactoryCanCreateApis() {
        Object accountsApi = unbluApiFactory.create("com.unblu.webapi.jersey.v4.api.AccountsApi");

        assertNotNull(accountsApi, "Factory should create API instances");
        assertInstanceOf(AccountsApi.class, accountsApi, "Factory should create correct API type");
    }

    @Test
    void testFactoryUsesInjectedApiClient() {
        // Create an API and verify it uses the injected ApiClient configuration
        AccountsApi accountsApi = (AccountsApi) unbluApiFactory.create("com.unblu.webapi.jersey.v4.api.AccountsApi");

        assertNotNull(accountsApi.getApiClient(), "Created API should have ApiClient");
        // Note: The factory creates new API instances with a reference to the injected apiClient
        assertNotNull(accountsApi.getApiClient().getBasePath(), "API should have configured base path");
    }
}
