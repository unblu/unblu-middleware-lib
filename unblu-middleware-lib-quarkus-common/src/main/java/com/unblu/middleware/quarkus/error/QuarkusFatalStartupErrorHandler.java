package com.unblu.middleware.quarkus.error;

import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class QuarkusFatalStartupErrorHandler implements FatalStartupErrorHandler {

    @Override
    public void shutdown() {
        log.error("Fatal startup error occurred. Shutting down application.");
        Quarkus.asyncExit(1);
    }
}
