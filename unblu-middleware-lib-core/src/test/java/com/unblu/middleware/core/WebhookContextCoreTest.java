package com.unblu.middleware.core;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.DefaultRequestQueueErrorHandler;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;
import com.unblu.middleware.webhooks.service.WebhookRegistrationService;
import com.unblu.middleware.webhooks.service.WebhookRequestHandlerImpl;
import com.unblu.middleware.webhooks.service.WebhookRequestQueue;
import com.unblu.webapi.model.v4.ConversationNewMessageEvent;
import com.unblu.webapi.model.v4.TextMessageData;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.unblu.middleware.common.utils.ObjectUtils.getObjectMapper;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.empty;

class WebhookContextCoreTest {

    @Test
    void onWebhookHandler_contextIsAsExpected() throws Exception {
        var latch = new CountDownLatch(2);
        var captured = new ConcurrentHashMap<String, Map<String, String>>();

        var objectMapper = getObjectMapper().copy();
        var contextRegistryWrapper = new ContextRegistryWrapper();
        contextRegistryWrapper.init();
        var webhookRegistrationService = mock(WebhookRegistrationService.class);
        when(webhookRegistrationService.isRegisteredFor(any(EventName.class))).thenReturn(true);

        var middlewareConfiguration = new MiddlewareConfiguration();
        middlewareConfiguration.setAutoRegister(false);
        middlewareConfiguration.setName("middleware");
        middlewareConfiguration.setUrl("https://dummy");

        var webhookHandler = new WebhookRequestHandlerImpl(
                new WebhookRequestQueue(() -> {
                }, contextRegistryWrapper, new DefaultRequestQueueErrorHandler()),
                middlewareConfiguration,
                webhookRegistrationService,
                objectMapper
        );

        webhookHandler.onWrappedWebhook(
                eventName("conversation.new_message"),
                ConversationNewMessageEvent.class,
                request -> {
                    var eventId = request.headers().firstValue("X-Unblu-Event-Id").orElse("missing");
                    captured.put(eventId, captureContext());
                    latch.countDown();
                    return empty();
                },
                WebhookHandlerOptions.contextSpec(ContextSpec.of(
                        "text", e -> e.body().getConversationMessage().getFallbackText(),
                        "conversationId", e -> e.body().getConversationMessage().getConversationId(),
                        "accountId", e -> e.body().getConversationMessage().getAccountId()
                ))
        );
        webhookHandler.assertSubscribed();

        var event123 = new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData()
                        .fallbackText("hello-123")
                        .conversationId("conv-123")
                        .accountId("account-123"));
        webhookHandler.handle(
                eventName("conversation.new_message"),
                objectMapper.writeValueAsBytes(event123),
                headers(Map.of("X-Unblu-Event-Id", "event-123"))
        );

        var event456 = new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData()
                        .fallbackText("hello-456")
                        .conversationId("conv-456")
                        .accountId("account-456"));
        webhookHandler.handle(
                eventName("conversation.new_message"),
                objectMapper.writeValueAsBytes(event456),
                headers(Map.of("X-Unblu-Event-Id", "event-456"))
        );

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        assertEquals("hello-123", captured.get("event-123").get("text"));
        assertEquals("conv-123", captured.get("event-123").get("conversationId"));
        assertEquals("account-123", captured.get("event-123").get("accountId"));
        assertEquals("event-123", captured.get("event-123").get("eventId"));

        assertEquals("hello-456", captured.get("event-456").get("text"));
        assertEquals("conv-456", captured.get("event-456").get("conversationId"));
        assertEquals("account-456", captured.get("event-456").get("accountId"));
        assertEquals("event-456", captured.get("event-456").get("eventId"));
    }

    private static HttpHeaders headers(Map<String, String> headers) {
        return HttpHeaders.of(headers.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()))), (_k, _v) -> true);
    }

    private static Map<String, String> captureContext() {
        var context = new HashMap<String, String>();
        context.put("text", MDC.get("text"));
        context.put("conversationId", MDC.get("conversationId"));
        context.put("accountId", MDC.get("accountId"));
        context.put("eventId", MDC.get("eventId"));
        return context;
    }
}
