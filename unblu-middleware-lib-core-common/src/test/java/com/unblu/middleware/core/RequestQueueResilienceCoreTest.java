package com.unblu.middleware.core;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestQueueResilienceCoreTest {

    private static final HttpHeaders NO_HEADERS = HttpHeaders.of(Map.of(), (_s1, _s2) -> true);

    private RequestQueue newQueue() {
        return new RequestQueue(
                () -> {
                },
                new ContextRegistryWrapper(),
                _error -> Mono.empty()
        );
    }

    @Test
    void synchronouslyThrowingHandlerDoesNotKillTheQueue() throws InterruptedException {
        var queue = newQueue();
        var processed = new ConcurrentLinkedQueue<String>();
        var latch = new CountDownLatch(1);

        queue.on(String.class,
                request -> {
                    if (request.startsWith("poison")) {
                        throw new NullPointerException("synchronous throw from handler");
                    }
                    processed.add(request);
                    latch.countDown();
                    return Mono.empty();
                },
                RequestOrderSpec.mustPreserveOrder(),
                ContextSpec.empty());

        queue.getFlux().subscribe();
        queue.queueRequest(new Request<>("poison", NO_HEADERS));
        queue.queueRequest(new Request<>("after-poison", NO_HEADERS));

        assertTrue(latch.await(5, TimeUnit.SECONDS), "queue stopped processing after a synchronous handler throw");
        assertEquals(List.of("after-poison"), List.copyOf(processed));
        queue.shutdown();
    }

    @Test
    void throwingKeyExtractorDoesNotKillTheQueue() throws InterruptedException {
        var queue = newQueue();
        var latch = new CountDownLatch(2);

        queue.on(String.class,
                request -> {
                    latch.countDown();
                    return Mono.empty();
                },
                RequestOrderSpec.mustPreserveOrderForThoseWithTheSame(request -> {
                    if (request.startsWith("poison")) {
                        throw new NullPointerException("throwing key extractor");
                    }
                    return request;
                }),
                ContextSpec.empty());

        queue.getFlux().subscribe();
        queue.queueRequest(new Request<>("poison", NO_HEADERS));
        queue.queueRequest(new Request<>("regular", NO_HEADERS));

        assertTrue(latch.await(5, TimeUnit.SECONDS), "queue stopped processing after a throwing key extractor");
        queue.shutdown();
    }
}
