package com.unblu.middleware.common.error;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Quarkus implementation of FatalStartupErrorHandler that shuts down the application.
 */
@ApplicationScoped
public class QuarkusFatalStartupErrorHandler implements FatalStartupErrorHandler {

    @Override
    public void shutdown() {
        Quarkus.asyncExit();
    }
}

