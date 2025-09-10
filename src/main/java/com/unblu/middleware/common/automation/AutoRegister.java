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
@ConditionalOnProperty(value = "unblu.middleware.autoRegister", havingValue = "true")
@Order(99)
public class AutoRegister implements ApplicationListener<ApplicationReadyEvent> {

    private final List<AutoRegistrable> autoRegistrableBeans;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Initializing auto-registration");
        autoRegistrableBeans.forEach(AutoRegistrable::autoRegister);
    }
}
