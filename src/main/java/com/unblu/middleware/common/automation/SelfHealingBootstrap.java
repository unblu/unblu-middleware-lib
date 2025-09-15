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
@ConditionalOnProperty(value = "unblu.middleware.selfHealingEnabled", havingValue = "true")
public class SelfHealingBootstrap {

    private final List<SelfHealing> selfHealingBeans;

    @Scheduled(initialDelayString = "${unblu.middleware.selfHealingCheckIntervalInSeconds}", fixedRateString = "${unblu.middleware.selfHealingCheckIntervalInSeconds}", timeUnit = TimeUnit.SECONDS)
    public void selfHealing() {
        log.debug("Launched self-healing");
        selfHealingBeans.forEach(SelfHealing::selfHeal);
    }
}
