package com.unblu.middleware.common.automation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "unblu.middleware.auto-subscribe", havingValue = "true")
@Order(100) // Ensure this runs after other configurations
public class AutoSubscribe implements ApplicationListener<ApplicationReadyEvent> {

    private final List<Subscribable> subscribableBeans;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Initializing auto-subscribe of {} bean(s)", subscribableBeans.size());
        subscribableBeans.forEach(Subscribable::assertSubscribed);
        log.info("Auto-subscribe process completed");
    }
}
