package com.unblu.middleware.common.automation;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles automatic registration of middleware components on startup.
 * Triggered when unblu.middleware.auto-register is true.
 */
@ApplicationScoped
@Slf4j
public class AutoRegister {

    @Inject
    MiddlewareConfiguration middlewareConfiguration;

    @Inject
    Instance<AutoRegistrable> autoRegistrableBeans;

    void onStart(@Observes StartupEvent event) {
        if (middlewareConfiguration.isAutoRegister()) {
            log.info("Initializing auto-registration of {} bean(s)", autoRegistrableBeans.stream().count());
            autoRegistrableBeans.forEach(AutoRegistrable::autoRegister);
            log.info("Auto-registration completed");
        }
    }
}

