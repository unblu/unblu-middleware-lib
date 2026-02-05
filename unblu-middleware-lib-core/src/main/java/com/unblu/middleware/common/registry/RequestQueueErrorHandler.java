package com.unblu.middleware.common.registry;

import reactor.core.publisher.Mono;

public interface RequestQueueErrorHandler {
    Mono<Void> handleError(Throwable error);
}
