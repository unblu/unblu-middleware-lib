package com.unblu.middleware.quarkus.automation;

import com.unblu.middleware.common.automation.AutoRegistrable;
import com.unblu.middleware.common.automation.SelfHealing;
import com.unblu.middleware.common.automation.Subscribable;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.webapi.jersey.v4.api.GlobalApi;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AutomationBootstrap {

    private final MiddlewareConfiguration middlewareConfiguration;
    private final Instance<AutoRegistrable> autoRegistrableBeans;
    private final Instance<Subscribable> subscribableBeans;
    private final Instance<SelfHealing> selfHealingBeans;
    private final Instance<GlobalApi> globalApi;

    private ScheduledExecutorService selfHealingExecutor;

    void onStart(@Observes @Priority(Interceptor.Priority.APPLICATION + 1000) StartupEvent event) {
        if (middlewareConfiguration.autoRegister()) {
            var autoRegistrables = StreamSupport.stream(autoRegistrableBeans.spliterator(), false).toList();
            log.info("Initializing auto-registration of {} bean(s)", autoRegistrables.size());
            autoRegistrables.forEach(AutoRegistrable::autoRegister);
            log.info("Auto-registration completed");
        }

        if (middlewareConfiguration.autoSubscribe()) {
            var subscribables = StreamSupport.stream(subscribableBeans.spliterator(), false).toList();
            log.info("Initializing auto-subscribe of {} bean(s)", subscribables.size());
            subscribables.forEach(Subscribable::assertSubscribed);
            log.info("Auto-subscribe process completed");
        }

        if (middlewareConfiguration.pingUnbluOnStartup() && globalApi.isResolvable()) {
            try {
                var pong = globalApi.get().globalPing();
                log.info("Unblu ping successful, Unblu server status: {}", pong.getStatus());
            } catch (Exception e) {
                log.error("Failed to ping Unblu server", e);
                throw new RuntimeException("Failed to ping Unblu server", e);
            }
        }

        if (middlewareConfiguration.selfHealingEnabled()) {
            var interval = middlewareConfiguration.selfHealingCheckIntervalInSeconds();
            selfHealingExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                var thread = new Thread(runnable, "unblu-middleware-self-healing");
                thread.setDaemon(true);
                return thread;
            });
            selfHealingExecutor.scheduleAtFixedRate(this::selfHeal, interval, interval, TimeUnit.SECONDS);
            log.info("Self-healing scheduled every {} seconds", interval);
        }
    }

    private void selfHeal() {
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

    void onStop(@Observes ShutdownEvent _event) {
        if (selfHealingExecutor != null) {
            selfHealingExecutor.shutdownNow();
        }
    }
}
