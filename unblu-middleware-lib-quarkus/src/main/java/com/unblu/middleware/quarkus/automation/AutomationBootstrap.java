package com.unblu.middleware.quarkus.automation;

import com.unblu.middleware.common.automation.AutoRegistrable;
import com.unblu.middleware.common.automation.Subscribable;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.StreamSupport;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AutomationBootstrap {

    private final MiddlewareConfiguration middlewareConfiguration;
    private final Instance<AutoRegistrable> autoRegistrableBeans;
    private final Instance<Subscribable> subscribableBeans;

    void onStart(@Observes @Priority(Interceptor.Priority.APPLICATION + 1000) StartupEvent event) {
        if (middlewareConfiguration.isAutoRegister()) {
            var autoRegistrables = StreamSupport.stream(autoRegistrableBeans.spliterator(), false).toList();
            log.info("Initializing auto-registration of {} bean(s)", autoRegistrables.size());
            autoRegistrables.forEach(AutoRegistrable::autoRegister);
            log.info("Auto-registration completed");
        }

        if (middlewareConfiguration.isAutoSubscribe()) {
            var subscribables = StreamSupport.stream(subscribableBeans.spliterator(), false).toList();
            log.info("Initializing auto-subscribe of {} bean(s)", subscribables.size());
            subscribables.forEach(Subscribable::assertSubscribed);
            log.info("Auto-subscribe process completed");
        }
    }
}
