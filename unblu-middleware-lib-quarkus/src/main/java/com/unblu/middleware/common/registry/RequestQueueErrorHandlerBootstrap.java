package com.unblu.middleware.common.registry;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import reactor.core.publisher.Mono;

/**
 * Provides RequestQueueErrorHandler implementations for different profiles.
 */
@ApplicationScoped
public class RequestQueueErrorHandlerBootstrap {

    @Produces
    @ApplicationScoped
    @IfBuildProfile("test")
    public RequestQueueErrorHandler testRequestQueueErrorHandler() {
        return throwable -> {
            if (throwable instanceof AssertionError) {
                return Mono.error(throwable); // Propagate assertion errors in test profile
            }
            return Mono.empty(); // No-op for other errors
        };
    }

    @Produces
    @ApplicationScoped
    @DefaultBean
    public RequestQueueErrorHandler defaultRequestQueueErrorHandler() {
        return new DefaultRequestQueueErrorHandler();
    }
}

