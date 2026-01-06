package com.unblu.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;
import com.unblu.middleware.webhooks.service.WebhookHandler;
import com.unblu.webapi.model.v4.ConversationNewMessageEvent;
import com.unblu.webapi.model.v4.TextMessageData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@DirtiesContext
class WebhookContextTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebhookHandler webhookHandler;

    @Autowired
    WebhookConfiguration webhookConfiguration;

    @Autowired
    WebTestClient webTestClient;


    @Test
    void onWebhookHandler_contextIsAsExpected() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        webhookHandler.onWrappedWebhook(eventName("conversation.new_message"), ConversationNewMessageEvent.class,
                request -> {

                    if (Objects.equals(request.headers().getFirst("X-Unblu-Event-Id"), "event-123")) {
                        latch.countDown();
                        assertThat(MDC.get("text")).isEqualTo("hello-123");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-123");
                        assertThat(MDC.get("accountId")).isEqualTo("account-123");

                        // defaults too
                        assertThat(MDC.get("eventId")).isEqualTo("event-123");
                        return Mono.just("ok").then();
                    }

                    if (Objects.equals(request.headers().getFirst("X-Unblu-Event-Id"), "event-456")) {
                        latch.countDown();
                        assertThat(MDC.get("text")).isEqualTo("hello-456");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-456");
                        assertThat(MDC.get("accountId")).isEqualTo("account-456");

                        // defaults too
                        assertThat(MDC.get("eventId")).isEqualTo("event-456");
                        return Mono.just("ok").then();
                    }

                    return Mono.error(new RuntimeException("Unexpected event id"));
                },
                WebhookHandlerOptions.contextSpec(ContextSpec.of(
                        "text", e -> e.body().getConversationMessage().getFallbackText(),
                        "conversationId", e -> e.body().getConversationMessage().getConversationId(),
                        "accountId", e -> e.body().getConversationMessage().getAccountId()
                )));

        webhookHandler.assertSubscribed();

        send("conversation.new_message", "event-123", new ConversationNewMessageEvent()
                .conversationMessage(
                        new TextMessageData()
                                .fallbackText("hello-123")
                                .conversationId("conv-123")
                                .accountId("account-123")
                )
        );

        send("conversation.new_message", "event-456", new ConversationNewMessageEvent()
                .conversationMessage(
                        new TextMessageData()
                                .fallbackText("hello-456")
                                .conversationId("conv-456")
                                .accountId("account-456")
                )
        );

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
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
