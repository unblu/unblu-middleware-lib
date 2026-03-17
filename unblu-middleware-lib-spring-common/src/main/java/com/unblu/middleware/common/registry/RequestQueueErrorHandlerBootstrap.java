package com.unblu.middleware.common.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

@Configuration
class RequestQueueErrorHandlerBootstrap {

    @Bean
    @Primary
    @Profile("test")
    public RequestQueueErrorHandler testRequestQueueErrorHandler() {
        return throwable -> {
            if (throwable instanceof AssertionError) {
                return Mono.error(throwable); // Propagate assertion errors in test profile
            }
            return Mono.empty(); // No-op for other errors
        };
    }
}
