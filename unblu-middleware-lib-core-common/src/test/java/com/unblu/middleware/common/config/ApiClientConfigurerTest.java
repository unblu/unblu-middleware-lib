package com.unblu.middleware.common.config;

import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import com.unblu.webapi.jersey.v4.invoker.auth.Authentication;
import com.unblu.webapi.jersey.v4.invoker.auth.HttpBasicAuth;
import com.unblu.webapi.jersey.v4.invoker.auth.HttpBearerAuth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ApiClientConfigurerTest {

    @Test
    void basicAuthSetsUsernameAndPassword() {
        UnbluConfiguration cfg = new UnbluConfiguration("https://unblu.example", "/app/rest/v4",
                "admin", "secret", null, null, null);
        ApiClient apiClient = new ApiClient();

        ApiClientConfigurer.configure(apiClient, cfg);

        assertEquals("https://unblu.example/app/rest/v4", apiClient.getBasePath());
        HttpBasicAuth basic = findAuth(apiClient, HttpBasicAuth.class);
        assertEquals("admin", basic.getUsername());
        assertEquals("secret", basic.getPassword());
        assertNull(findAuth(apiClient, HttpBearerAuth.class).getBearerToken());
    }

    @Test
    void bearerTokenSetsBearerAuth() {
        UnbluConfiguration cfg = new UnbluConfiguration("https://unblu.example", "/app/rest/v4",
                null, null, "my-token", null, null);
        ApiClient apiClient = new ApiClient();

        ApiClientConfigurer.configure(apiClient, cfg);

        assertEquals("https://unblu.example/app/rest/v4", apiClient.getBasePath());
        assertEquals("my-token", findAuth(apiClient, HttpBearerAuth.class).getBearerToken());
        HttpBasicAuth basic = findAuth(apiClient, HttpBasicAuth.class);
        assertNull(basic.getUsername());
        assertNull(basic.getPassword());
    }

    private static <T extends Authentication> T findAuth(ApiClient apiClient, Class<T> type) {
        return apiClient.getAuthentications().values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No authentication of type " + type.getSimpleName() + " found"));
    }
}
