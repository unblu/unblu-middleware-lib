package com.unblu.middleware.core;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.common.registry.RequestQueueErrorHandler;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestQueue;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.service.WebhookRegistrationService;
import com.unblu.middleware.webhooks.service.WebhookRequestHandlerImpl;
import com.unblu.middleware.webhooks.service.WebhookRequestQueue;
import com.unblu.webapi.model.v4.BotDialogMessageRequest;
import com.unblu.webapi.model.v4.BotDialogMessageResponse;
import com.unblu.webapi.model.v4.ConversationNewMessageEvent;
import com.unblu.webapi.model.v4.TextMessageData;
import org.junit.jupiter.api.Test;

import java.net.http.HttpHeaders;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.unblu.middleware.common.utils.ObjectUtils.getObjectMapper;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.*;

class ProcessingContinuesOnErrorCoreTest {

    @Test
    void givenPreviousError_nextWebhookStillProcesses() throws Exception {
        var errorLatch = new CountDownLatch(1);
        var okLatch = new CountDownLatch(1);

        var objectMapper = getObjectMapper().copy();
        var contextRegistryWrapper = new ContextRegistryWrapper();
        RequestQueueErrorHandler queueErrorHandler = throwable -> throwable instanceof AssertionError ? error(throwable) : empty();

        var webhookRegistrationService = mock(WebhookRegistrationService.class);
        when(webhookRegistrationService.isRegisteredFor(any(EventName.class))).thenReturn(true);

        var middlewareConfiguration = new MiddlewareConfiguration(
                "middleware",
                "",
                "https://dummy",
                false,
                true,
                true,
                60L,
                true
        );

        var webhookRequestQueue = new WebhookRequestQueue(() -> {
        }, contextRegistryWrapper, queueErrorHandler);
        var webhookHandler = new WebhookRequestHandlerImpl(
                webhookRequestQueue,
                middlewareConfiguration,
                webhookRegistrationService,
                objectMapper
        );

        webhookHandler.onWebhook(eventName("conversation.new_message"), ConversationNewMessageEvent.class,
                request -> {
                    if ("error".equals(request.getConversationMessage().getFallbackText())) {
                        errorLatch.countDown();
                        return error(new RuntimeException("Simulated processing error"));
                    }
                    okLatch.countDown();
                    return just("ok").then();
                });
        webhookHandler.assertSubscribed();

        var first = new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData().fallbackText("error"));
        var second = new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData().fallbackText("all good"));

        webhookHandler.handle(eventName("conversation.new_message"), objectMapper.writeValueAsBytes(first), emptyHeaders());
        webhookHandler.handle(eventName("conversation.new_message"), objectMapper.writeValueAsBytes(second), emptyHeaders());

        assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
        assertTrue(okLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    void givenPreviousError_nextOutboundStillProcesses() throws Exception {
        var errorLatch = new CountDownLatch(1);
        var okLatch = new CountDownLatch(1);

        var contextRegistryWrapper = new ContextRegistryWrapper();
        RequestQueueErrorHandler queueErrorHandler = throwable -> throwable instanceof AssertionError ? error(throwable) : empty();

        var outboundRequestQueue = new OutboundRequestQueue(() -> {
        }, contextRegistryWrapper, queueErrorHandler);
        var outboundRequestHandler = new OutboundRequestHandler(outboundRequestQueue, getObjectMapper().copy(), contextRegistryWrapper);

        outboundRequestHandler.on(
                outboundRequestType("outbound.bot.dialog.message"),
                BotDialogMessageRequest.class,
                BotDialogMessageResponse.class,
                _request -> just(new BotDialogMessageResponse()),
                request -> {
                    if ("error".equals(request.getConversationMessage().getFallbackText())) {
                        errorLatch.countDown();
                        return error(new RuntimeException("Simulated processing error"));
                    }
                    okLatch.countDown();
                    return just("ok").then();
                },
                OutboundRequestHandlerOptions.<BotDialogMessageRequest>requestOrderSpec(RequestOrderSpec.canIgnoreOrder())
                        .withContextSpec(ContextSpec.empty())
        );
        outboundRequestHandler.assertSubscribed();

        var first = new BotDialogMessageRequest()
                .conversationMessage(new TextMessageData().fallbackText("error"));
        var second = new BotDialogMessageRequest()
                .conversationMessage(new TextMessageData().fallbackText("all good"));

        outboundRequestHandler.handle(
                outboundRequestType("outbound.bot.dialog.message"),
                new Request<>(first, emptyHeaders())
        ).block();
        outboundRequestHandler.handle(
                outboundRequestType("outbound.bot.dialog.message"),
                new Request<>(second, emptyHeaders())
        ).block();

        assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
        assertTrue(okLatch.await(5, TimeUnit.SECONDS));
    }

    private static HttpHeaders emptyHeaders() {
        return HttpHeaders.of(Map.of(), (_k, _v) -> true);
    }
}
