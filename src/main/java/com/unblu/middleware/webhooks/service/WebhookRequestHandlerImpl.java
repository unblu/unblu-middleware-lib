package com.unblu.middleware.webhooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.middleware.webhooks.entity.EventName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.unblu.middleware.common.request.RequestHandler.withRequestContext;
import static com.unblu.middleware.webhooks.util.WebhookContextSpecUtil.webhookContextSpec;

@Service
@Slf4j
public class WebhookRequestHandlerImpl extends RequestQueueServiceImpl implements WebhookHandlerService, WebhookRequestHandler {

    private final MiddlewareConfiguration middlewareConfiguration;
    private final WebhookRegistrationService webhookRegistrationService;
    private final ObjectMapper objectMapper;
    private final Map<EventName, Class<?>> eventTypeMap = new ConcurrentHashMap<>();

    public WebhookRequestHandlerImpl(RequestQueue requestQueue, MiddlewareConfiguration middlewareConfiguration, WebhookRegistrationService webhookRegistrationService, ObjectMapper objectMapper) {
        super(requestQueue);
        this.middlewareConfiguration = middlewareConfiguration;
        this.webhookRegistrationService = webhookRegistrationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> void onWrappedWebhook(@NonNull EventName eventName, @NonNull Class<T> expectedType, @NonNull Function<Request<T>, Mono<Void>> processAction, @NonNull RequestOrderSpec<Request<T>> requestOrderSpec, @NonNull ContextSpec<Request<T>> contextSpec) {
        checkThatIsRegisteredFor(eventName);
        eventTypeMap.put(eventName, expectedType);
        requestQueue.onWrapped(expectedType, processAction, requestOrderSpec, contextSpec.with(webhookContextSpec()));
    }

    private void checkThatIsRegisteredFor(EventName eventName) {
        if (middlewareConfiguration.isAutoRegister()) {
            webhookRegistrationService.assertRegistered(eventName);
        } else if (!webhookRegistrationService.isRegisteredFor(eventName)) {
            log.info("While registering a handler for webhook event {}, we detected that the event was not registered " +
                    "in an Unblu webhook registration managed by the webhookRegistrationService and the library is " +
                    "not configured to auto-register webhooks. Make sure you registered it manually.", eventName);
        }
    }

    @Override
    public void handle(EventName eventName, byte[] body, HttpHeaders headers) {
        var expectedType = eventTypeMap.get(eventName);
        if (expectedType == null) {
            throw new NoHandlerException("No handler registered for event: " + eventName);
        }
        try {
            var requestBody = objectMapper.readValue(body, expectedType);
            requestQueue.queueRequest(new Request<>(requestBody, headers));
        } catch (IOException e) {
            throw new InvalidRequestException(withRequestContext("Failed to parse webhook", headers), e);
        }
    }

    @Override
    public void subscribe() {
        getFlux().subscribe();
    }

    @Override
    public Flux<Void> getFlux() {
        return requestQueue.getFlux();
    }
}
