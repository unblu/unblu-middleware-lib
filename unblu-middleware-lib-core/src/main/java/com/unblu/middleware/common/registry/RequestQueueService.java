package com.unblu.middleware.common.registry;

import com.unblu.middleware.common.automation.Subscribable;
import reactor.core.publisher.Flux;

public interface RequestQueueService extends Subscribable {
    Flux<Void> getFlux();
    void shutdown();
}
