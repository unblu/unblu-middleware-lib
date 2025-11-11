package com.unblu.middleware.webhooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;
import com.unblu.webapi.model.v4.WebhookPingEvent;
import jakarta.annotation.PostConstruct;
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

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrappedHeaderSpec;
import static com.unblu.middleware.webhooks.util.WebhookContextSpecUtil.webhookHeadersContextSpec;

@Service
@Slf4j
public class WebhookRequestHandlerImpl extends RequestQueueServiceImpl implements WebhookHandler, WebhookRequestHandler {

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

    @PostConstruct
    private void registerWebhookPingHandler() {
        onWebhook(EventName.eventName("ping"), WebhookPingEvent.class,
                request -> Mono.fromRunnable(() -> log.info("Received webhook ping event")));
    }

    @Override
    public <T> void onWrappedWebhook(@NonNull EventName eventName,
                                     @NonNull Class<T> expectedType,
                                     @NonNull Function<Request<T>, Mono<Void>> processAction,
                                     @NonNull WebhookHandlerOptions<Request<T>> webhookHandlerOptions) {
        if (webhookHandlerOptions.shouldAssertRegistered()) {
            checkThatIsRegisteredFor(eventName);
        }
        eventTypeMap.put(eventName, expectedType);
        requestQueue.onWrapped(expectedType, processAction, webhookHandlerOptions.requestOrderSpec(), webhookHandlerOptions.contextSpec().with(wrappedHeaderSpec(webhookHeadersContextSpec())));
    }

    private void checkThatIsRegisteredFor(EventName eventName) {
        if (middlewareConfiguration.isAutoRegister()) {
            webhookRegistrationService.assertRegistered(eventName);
        } else if (!webhookRegistrationService.isRegisteredFor(eventName)) {
            log.warn("While registering a handler for webhook event {}, we detected that the event was not registered " +
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
            throw new InvalidRequestException("Failed to parse webhook", e);
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
