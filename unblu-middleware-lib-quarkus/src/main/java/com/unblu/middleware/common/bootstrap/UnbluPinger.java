package com.unblu.middleware.common.bootstrap;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.webapi.jersey.v4.api.GlobalApi;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Pings Unblu server on startup to verify connectivity.
 * Triggered when unblu.middleware.ping-unblu-on-startup is true.
 */
@ApplicationScoped
@Slf4j
public class UnbluPinger {

    @Inject
    MiddlewareConfiguration middlewareConfiguration;

    @Inject
    GlobalApi globalApi;

    void onStart(@Observes StartupEvent event) {
        if (middlewareConfiguration.isPingUnbluOnStartup()) {
            pingUnblu();
        }
    }

    private void pingUnblu() {
        try {
            var pong = globalApi.globalPing();
            log.info("Unblu ping successful, Unblu server status: {}", pong.getStatus());
        } catch (Exception e) {
            log.error("Failed to ping Unblu server", e);
            throw new RuntimeException("Failed to ping Unblu server", e);
        }
    }
}

