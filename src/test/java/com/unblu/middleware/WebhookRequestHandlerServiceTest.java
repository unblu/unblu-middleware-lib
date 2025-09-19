package com.unblu.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.service.WebhookHandlerService;
import com.unblu.webapi.model.v4.ConversationNewMessageEvent;
import com.unblu.webapi.model.v4.TextMessageData;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.List;

import static com.unblu.middleware.common.entity.ContextEntrySpec.contextOf;
import static com.unblu.middleware.common.registry.RequestOrderSpec.canIgnoreOrder;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@TestPropertySource(properties = {
        "unblu.webhook.eventNames=conversation.new_message1,conversation.new_message2,conversation.new_message3",
})
class WebhookRequestHandlerServiceTest {

    @Autowired
    WebhookHandlerService webhookHandlerService;

    @Autowired
    WebhookConfiguration webhookConfiguration;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void registeredWebhookHandler_isCalledOnWebhook() {
        var messageHandler = new NewMessageHandler();

        webhookHandlerService.onWebhook(
                eventName("conversation.new_message1"),
                ConversationNewMessageEvent.class,
                messageHandler::handle,
                canIgnoreOrder()
        );
        webhookHandlerService.subscribe();

        var event = new ConversationNewMessageEvent()
                .eventType("conversation.new_message1")
                .accountId("whatever")
                .conversationMessage(new TextMessageData()
                        .text("hello world"));
        send("conversation.new_message1", "event1", event);

        await().atMost(2, SECONDS).until(() -> messageHandler.lastMessageEvent != null);
        assertThat(messageHandler.lastMessageEvent)
                .isEqualTo(event);
    }

    @Test
    void registeredWebhookHandlerWithHeader_isCalledOnWebhook() {
        var messageHandler = new NewMessageHandler();

        webhookHandlerService.onWrappedWebhook(
                eventName("conversation.new_message2"),
                ConversationNewMessageEvent.class,
                e -> messageHandler.handle(e.body()),
                canIgnoreOrder()
        );
        webhookHandlerService.subscribe();

        var event = new ConversationNewMessageEvent()
                .eventType("conversation.new_message2")
                .accountId("whatever")
                .conversationMessage(new TextMessageData()
                        .text("hello world"));
        send("conversation.new_message2", "event1", event);

        await().atMost(2, SECONDS).until(() -> messageHandler.lastMessageEvent != null);
        assertThat(messageHandler.lastMessageEvent)
                .isEqualTo(event);
    }

    @Test
    void registeredWebhookHandlerWithHeader_onWebhook_contextIsCorrect() {
        var messageHandler = new NewMessageHandler();

        webhookHandlerService.onWrappedWebhook(
                eventName("conversation.new_message3"),
                ConversationNewMessageEvent.class,
                e -> Mono.just(e).transformDeferredContextual((e1, ctx) -> messageHandler.withContextView(ctx)),
                canIgnoreOrder(),
                List.of(
                        contextOf("eventId", e -> e.headers().getFirst("X-Unblu-Event-Id")),
                        contextOf("accountId", e -> e.body().getAccountId()),
                        contextOf("methodName", _e -> "myMethodName3")
                )
        );
        webhookHandlerService.subscribe();

        var event = new ConversationNewMessageEvent()
                .eventType("conversation.new_message3")
                .accountId("whateverAccountId3")
                .conversationMessage(new TextMessageData()
                        .text("hello world"));
        send("conversation.new_message3", "event3", event);

        await().atMost(2, SECONDS).until(() -> messageHandler.lastContextView != null);
        assertThat(messageHandler.lastContextView)
                .has(keyWithValue("eventId", "event3"))
                .has(keyWithValue("accountId", "whateverAccountId3"))
                .has(keyWithValue("methodName", "myMethodName3"));
    }

    private Condition<? super ContextView> keyWithValue(String key, String value) {
        return new Condition<>(contextView ->
                contextView.hasKey(key) && value.equals(contextView.get(key)),
                "ContextView must contain key '%s' with value '%s'",
                key,
                value
        );
    }

    @Getter
    private static class NewMessageHandler {

        private ConversationNewMessageEvent lastMessageEvent;
        private ContextView lastContextView;

        public Mono<Void> handle(ConversationNewMessageEvent event) {
            log.info("Handling new message event: {}", event);
            lastMessageEvent = event;
            return Mono.empty();
        }

        public Mono<Void> withContextView(ContextView contextView) {
            log.info("Context view is: {}", contextView);
            lastContextView = contextView;
            return Mono.empty();
        }
    }

    @SneakyThrows
    private void send(String eventName, String eventId, Object eventData) {
        var body = objectMapper.writeValueAsString(eventData);
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", eventName)
                .header("X-Unblu-Event-Id", eventId)
                .header("X-Unblu-Signature", calculateSignature(body))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    private String calculateSignature(Object body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webhookConfiguration.getSecret()).hmacHex(body.toString().getBytes());
    }
}
