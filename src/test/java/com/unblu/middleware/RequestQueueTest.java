package com.unblu.middleware;

import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import io.micrometer.context.ContextRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import reactor.test.StepVerifier;

@Slf4j
class RequestQueueTest {

    @Test
    void requestQueueCompletesOnShutdown() {
        var requestQueue = new RequestQueue(new FatalStartupErrorHandler(null), new ContextRegistryWrapper(new ContextRegistry()));
        StepVerifier.create(requestQueue.getFlux().then())
                .then(() -> requestQueue.queueRequest(new Request<>("test", new HttpHeaders())))
                .then(requestQueue::shutdown)
                .verifyComplete();
    }
}
