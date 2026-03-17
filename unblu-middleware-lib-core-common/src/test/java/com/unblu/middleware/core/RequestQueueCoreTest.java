package com.unblu.middleware.core;

import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.http.HttpHeaders;
import java.util.Map;

class RequestQueueCoreTest {

    @Test
    void requestQueueCompletesOnShutdown() {
        var requestQueue = new RequestQueue(
                () -> {
                },
                new ContextRegistryWrapper(),
                _error -> Mono.error(_error)
        );

        StepVerifier.create(requestQueue.getFlux().then())
                .then(() -> requestQueue.queueRequest(new Request<>("test", HttpHeaders.of(Map.of(), (_s1, _s2) -> true))))
                .then(requestQueue::shutdown)
                .verifyComplete();
    }
}

