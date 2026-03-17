package com.unblu.middleware.bootstrap.unbluapi;

import com.unblu.webapi.jersey.v4.invoker.ApiClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UnbluApiFactory {

    @Inject
    ApiClient apiClient;

    public Object create(String fqcn) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> c = Class.forName(fqcn, true, cl);
            return c.getDeclaredConstructor(ApiClient.class).newInstance(apiClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create " + fqcn, e);
        }
    }
}
