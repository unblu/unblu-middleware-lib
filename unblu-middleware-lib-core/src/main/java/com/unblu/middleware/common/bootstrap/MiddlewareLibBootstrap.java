package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import io.micrometer.context.ContextRegistry;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

public class MiddlewareLibBootstrap {

    @Named
    @Singleton
    public ContextRegistry contextRegistry() {
        return ContextRegistry.getInstance();
    }

    @Named
    @Singleton
    public JSON jerseyJsonResolver() {
        return new JSON();
    }

    @Named
    @Singleton
    public ObjectMapper objectMapper(JSON jerseyJsonResolver) {
        return jerseyJsonResolver.getContext(Object.class);
    }
}
