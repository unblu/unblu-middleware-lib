package com.unblu.middleware.common.registry;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Named
@Singleton
public class DefaultRequestQueueErrorHandler implements RequestQueueErrorHandler {

    @Override
    public Mono<Void> handleError(Throwable error) {
        return Mono.empty();  // No-op error handler (resume operation)
    }
}
