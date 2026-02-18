package com.unblu.middleware.common.automation;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles automatic subscription of middleware components on startup.
 * Triggered when unblu.middleware.auto-subscribe is true.
 */
@ApplicationScoped
@Slf4j
public class AutoSubscribe {

    @Inject
    MiddlewareConfiguration middlewareConfiguration;

    @Inject
    Instance<Subscribable> subscribableBeans;

    void onStart(@Observes StartupEvent event) {
        if (middlewareConfiguration.isAutoSubscribe()) {
            log.info("Initializing auto-subscribe of {} bean(s)", subscribableBeans.stream().count());
            subscribableBeans.forEach(Subscribable::assertSubscribed);
            log.info("Auto-subscribe process completed");
        }
    }
}

