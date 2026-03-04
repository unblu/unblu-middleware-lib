package com.unblu.middleware.common.bootstrap;

import com.unblu.webapi.jersey.v4.api.GlobalApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBooleanProperty("unblu.middleware.ping-unblu-on-startup")
class UnbluPinger {

    private final GlobalApi globalApi;

    @EventListener(ApplicationReadyEvent.class)
    void pingUnblu() {
        try {
            var pong = globalApi.globalPing();
            log.info("Unblu ping successful, Unblu server status: {}", pong.getStatus());
        } catch (Exception e) {
            log.error("Failed to ping Unblu server", e);
            throw new RuntimeException("Failed to ping Unblu server", e);
        }
    }
}
