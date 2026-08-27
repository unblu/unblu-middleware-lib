package com.unblu.middleware.core;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;
import java.time.Duration;
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

    @Test
    void sameKeyAsyncHandlersProcessInArrivalOrder() throws InterruptedException {
        var queue = newQueue();
        var completions = new ConcurrentLinkedQueue<String>();
        var latch = new CountDownLatch(2);

        queue.on(String.class,
                request -> Mono.delay(request.equals("first") ? Duration.ofMillis(300) : Duration.ofMillis(1))
                        .doOnNext(_t -> {
                            completions.add(request);
                            latch.countDown();
                        })
                        .then(),
                RequestOrderSpec.mustPreserveOrderForThoseWithTheSame(_request -> "same-key"),
                ContextSpec.empty());

        queue.getFlux().subscribe();
        queue.queueRequest(new Request<>("first", NO_HEADERS));
        queue.queueRequest(new Request<>("second", NO_HEADERS));

        assertTrue(latch.await(5, TimeUnit.SECONDS), "requests were not processed");
        assertEquals(List.of("first", "second"), List.copyOf(completions),
                "same-key async requests must complete in arrival order");
        queue.shutdown();
    }

    @Test
    void shutdownDrainsAlreadyQueuedRequests() throws InterruptedException {
        var queue = newQueue();
        var processed = new ConcurrentLinkedQueue<String>();

        queue.on(String.class,
                request -> Mono.delay(Duration.ofMillis(100))
                        .doOnNext(_t -> processed.add(request))
                        .then(),
                RequestOrderSpec.mustPreserveOrder(),
                ContextSpec.empty());

        queue.getFlux().subscribe();
        queue.queueRequest(new Request<>("one", NO_HEADERS));
        queue.queueRequest(new Request<>("two", NO_HEADERS));

        queue.shutdown(); // must block until already-acknowledged requests are processed

        assertEquals(List.of("one", "two"), List.copyOf(processed),
                "requests queued before shutdown must still be processed");
    }
}
