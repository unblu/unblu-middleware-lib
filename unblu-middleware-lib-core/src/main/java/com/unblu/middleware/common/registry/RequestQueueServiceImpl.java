package com.unblu.middleware.common.registry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class RequestQueueServiceImpl implements RequestQueueService {
    protected final RequestQueue requestQueue;

    @Getter
    private boolean subscribed = false;

    @Override
    public Flux<Void> getFlux() {
        return requestQueue.getFlux();
    }

    @Override
    public void subscribe() {
        getFlux().subscribe();
        subscribed = true;
    }

    @Override
    public void shutdown() {
        requestQueue.shutdown();
    }
}
