package com.unblu.middleware.common.automation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@ConditionalOnProperty(value = "unblu.middleware.self-healing-enabled", havingValue = "true")
class SelfHealingBootstrap {

    private final List<SelfHealing> selfHealingBeans;

    // the documented kebab-case key wins; the camelCase key (supplied by the bundled
    // middleware-application.yml) remains as fallback so existing setups keep working
    private static final String INTERVAL = "${unblu.middleware.self-healing-check-interval-in-seconds:${unblu.middleware.selfHealingCheckIntervalInSeconds:60}}";

    @Scheduled(initialDelayString = INTERVAL, fixedRateString = INTERVAL, timeUnit = TimeUnit.SECONDS)
    public void selfHealing() {
        log.debug("Launched self-healing");
        selfHealingBeans.forEach(bean -> {
            try {
                bean.selfHeal();
            } catch (RuntimeException e) {
                // one failing bean must not starve self-healing for the remaining beans
                log.error("Self-healing failed for {}", bean.getClass().getSimpleName(), e);
            }
        });
    }
}
