package com.unblu.middleware.common.automation;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Periodically triggers self-healing for all SelfHealing beans.
 * Enabled when unblu.middleware.self-healing-enabled is true.
 */
@ApplicationScoped
@Slf4j
public class SelfHealingBootstrap {

    @Inject
    Instance<SelfHealing> selfHealingBeans;

    @Scheduled(
            delay = 60,
            every = "{unblu.middleware.self-healing-check-interval-in-seconds:60}s",
            skipExecutionIf = SelfHealingDisabled.class
    )
    public void selfHealing() {
        log.debug("Launched self-healing");
        selfHealingBeans.forEach(SelfHealing::selfHeal);
    }

    public static class SelfHealingDisabled implements Scheduled.SkipPredicate {

        @Inject
        MiddlewareConfiguration middlewareConfiguration;

        @Override
        public boolean test(ScheduledExecution execution) {
            return !middlewareConfiguration.isSelfHealingEnabled();
        }
    }
}

