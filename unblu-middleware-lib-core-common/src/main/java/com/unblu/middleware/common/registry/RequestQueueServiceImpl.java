package com.unblu.middleware.common.registry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class RequestQueueServiceImpl implements RequestQueueService {
    protected final RequestQueue requestQueue;

    @Getter
    private volatile boolean subscribed = false;

    @Override
    public Flux<Void> getFlux() {
        return requestQueue.getFlux();
    }

    @Override
    public void subscribe() {
        // safety net: the pipeline guards against user-code throws, but if it still
        // terminates with an error, resubscribe instead of silently dropping all
        // subsequent requests (a completed pipeline — shutdown — is not retried)
        getFlux()
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(100))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(signal -> log.error("Request queue pipeline terminated unexpectedly; resubscribing", signal.failure())))
                .subscribe(_v -> {
                }, e -> log.error("Request queue pipeline terminated permanently", e));
        subscribed = true;
    }

    @Override
    public void shutdown() {
        requestQueue.shutdown();
    }
}
