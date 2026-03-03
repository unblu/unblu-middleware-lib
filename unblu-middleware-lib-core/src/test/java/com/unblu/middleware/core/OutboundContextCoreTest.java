package com.unblu.middleware.core;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.DefaultRequestQueueErrorHandler;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestQueue;
import com.unblu.webapi.model.v4.BotDialogMessageRequest;
import com.unblu.webapi.model.v4.BotDialogMessageResponse;
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

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

class OutboundContextCoreTest {

    @Test
    void onOutboundRequestHandler_contextIsAsExpected() throws Exception {
        var syncLatch = new CountDownLatch(2);
        var asyncLatch = new CountDownLatch(2);
        var syncCaptured = new ConcurrentHashMap<String, Map<String, String>>();
        var asyncCaptured = new ConcurrentHashMap<String, Map<String, String>>();

        var contextRegistryWrapper = new ContextRegistryWrapper();
        contextRegistryWrapper.init();
        var outboundRequestHandler = new OutboundRequestHandler(
                new OutboundRequestQueue(() -> {
                }, contextRegistryWrapper, new DefaultRequestQueueErrorHandler()),
                com.unblu.middleware.common.utils.ObjectUtils.getObjectMapper().copy(),
                contextRegistryWrapper
        );

        outboundRequestHandler.onWrapped(
                outboundRequestType("outbound.bot.dialog.message"),
                BotDialogMessageRequest.class,
                BotDialogMessageResponse.class,
                request -> {
                    var invocationId = request.headers().firstValue("X-Unblu-Invocation-ID").orElse("missing");
                    syncCaptured.put(invocationId, captureContext());
                    syncLatch.countDown();
                    return just(new BotDialogMessageResponse());
                },
                request -> {
                    var invocationId = request.headers().firstValue("X-Unblu-Invocation-ID").orElse("missing");
                    asyncCaptured.put(invocationId, captureContext());
                    asyncLatch.countDown();
                    return empty();
                },
                RequestOrderSpec.canIgnoreOrder(),
                ContextSpec.of(
                        "text", e -> e.body().getConversationMessage().getFallbackText(),
                        "conversationId", e -> e.body().getConversationMessage().getConversationId(),
                        "accountId", e -> e.body().getConversationMessage().getAccountId()
                )
        );
        outboundRequestHandler.assertSubscribed();

        outboundRequestHandler.handle(
                outboundRequestType("outbound.bot.dialog.message"),
                new Request<>(new BotDialogMessageRequest()
                        .conversationMessage(new TextMessageData()
                                .fallbackText("hello-123")
                                .conversationId("conv-123")
                                .accountId("account-123")), headers(Map.of(
                        "X-Unblu-Invocation-ID", "invocation-123",
                        "X-Unblu-Delivery", "delivery-123",
                        "X-Unblu-Retry-No", "123"
                )))
        ).block();

        outboundRequestHandler.handle(
                outboundRequestType("outbound.bot.dialog.message"),
                new Request<>(new BotDialogMessageRequest()
                        .conversationMessage(new TextMessageData()
                                .fallbackText("hello-456")
                                .conversationId("conv-456")
                                .accountId("account-456")), headers(Map.of(
                        "X-Unblu-Invocation-ID", "invocation-456",
                        "X-Unblu-Delivery", "delivery-456",
                        "X-Unblu-Retry-No", "456"
                )))
        ).block();

        assertTrue(syncLatch.await(5, TimeUnit.SECONDS));
        assertTrue(asyncLatch.await(5, TimeUnit.SECONDS));

        assertContext(syncCaptured.get("invocation-123"), "hello-123", "conv-123", "account-123", "delivery-123", "invocation-123", "123");
        assertContext(asyncCaptured.get("invocation-123"), "hello-123", "conv-123", "account-123", "delivery-123", "invocation-123", "123");

        assertContext(syncCaptured.get("invocation-456"), "hello-456", "conv-456", "account-456", "delivery-456", "invocation-456", "456");
        assertContext(asyncCaptured.get("invocation-456"), "hello-456", "conv-456", "account-456", "delivery-456", "invocation-456", "456");
    }

    private static void assertContext(Map<String, String> actual, String text, String conversationId, String accountId, String deliveryId, String invocationId, String retryNo) {
        assertEquals(text, actual.get("text"));
        assertEquals(conversationId, actual.get("conversationId"));
        assertEquals(accountId, actual.get("accountId"));
        assertEquals(deliveryId, actual.get("deliveryId"));
        assertEquals(invocationId, actual.get("invocationId"));
        assertEquals(retryNo, actual.get("retryNo"));
    }

    private static Map<String, String> captureContext() {
        var context = new HashMap<String, String>();
        context.put("text", MDC.get("text"));
        context.put("conversationId", MDC.get("conversationId"));
        context.put("accountId", MDC.get("accountId"));
        context.put("deliveryId", MDC.get("deliveryId"));
        context.put("invocationId", MDC.get("invocationId"));
        context.put("retryNo", MDC.get("retryNo"));
        return context;
    }

    private static HttpHeaders headers(Map<String, String> headers) {
        return HttpHeaders.of(headers.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()))), (_k, _v) -> true);
    }
}
