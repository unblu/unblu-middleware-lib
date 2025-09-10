package com.unblu.middleware.common.registry;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class RequestQueueServiceImpl implements RequestQueueService {
    protected final RequestQueue requestQueue;

    @Override
    public Flux<Void> getFlux() {
        return requestQueue.getFlux();
    }

    @Override
    public void subscribe() {
        getFlux().subscribe();
    }

    @Override
    public void shutdown() {
        requestQueue.shutdown();
    }
}
