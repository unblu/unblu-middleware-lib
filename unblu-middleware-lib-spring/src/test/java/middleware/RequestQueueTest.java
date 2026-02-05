package middleware;

import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.SpringFatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import io.micrometer.context.ContextRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.http.HttpHeaders;
import java.util.Map;

@Slf4j
class RequestQueueTest {

    @Test
    void requestQueueCompletesOnShutdown() {
        var requestQueue = new RequestQueue(new SpringFatalStartupErrorHandler(null), new ContextRegistryWrapper(new ContextRegistry()), Mono::error);
        StepVerifier.create(requestQueue.getFlux().then())
                .then(() -> requestQueue.queueRequest(new Request<>("test", HttpHeaders.of(Map.of(), (s1, s2) -> true))))
                .then(requestQueue::shutdown)
                .verifyComplete();
    }
}
