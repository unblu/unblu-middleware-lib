package com.unblu.middleware.config;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.config.UnbluConfiguration;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * CDI producer for common configuration beans that converts Quarkus config
 * mappings to the core library's configuration records.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationProducer {

    private final SmallRyeConfig config;
    private final DtoBinder binder;

    @Produces
    @Singleton
    public UnbluConfiguration unbluConfiguration() {
        return binder.bindPrefix(config, "unblu", UnbluConfiguration.class);
    }

    @Produces
    @Singleton
    public MiddlewareConfiguration middlewareConfiguration() {
        return binder.bindPrefix(config, "unblu.middleware", MiddlewareConfiguration.class);
    }
}
