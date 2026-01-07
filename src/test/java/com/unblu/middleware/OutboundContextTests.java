package com.unblu.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.webapi.model.v4.BotDialogMessageRequest;
import com.unblu.webapi.model.v4.BotDialogMessageResponse;
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

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@DirtiesContext
class OutboundContextTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OutboundRequestHandler outboundRequestHandler;

    @Autowired
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Autowired
    WebTestClient webTestClient;


    @Test
    void onOutboundRequestHandler_contextIsAsExpected() throws InterruptedException {
        CountDownLatch syncLatch = new CountDownLatch(2);
        CountDownLatch asyncLatch = new CountDownLatch(2);

        outboundRequestHandler.onWrapped(outboundRequestType("outbound.bot.dialog.message"), BotDialogMessageRequest.class, BotDialogMessageResponse.class,
                // checking context in sync handler
                request -> {
                    if (Objects.equals(request.headers().getFirst("X-Unblu-Invocation-ID"), "invocation-123")) {
                        assertThat(MDC.get("text")).isEqualTo("hello-123");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-123");
                        assertThat(MDC.get("accountId")).isEqualTo("account-123");

                        // defaults too
                        assertThat(MDC.get("deliveryId")).isEqualTo("delivery-123");
                        assertThat(MDC.get("invocationId")).isEqualTo("invocation-123");
                        assertThat(MDC.get("retryNo")).isEqualTo("123");

                        syncLatch.countDown();
                        return Mono.just(new BotDialogMessageResponse());
                    }

                    if (Objects.equals(request.headers().getFirst("X-Unblu-Invocation-ID"), "invocation-456")) {
                        assertThat(MDC.get("text")).isEqualTo("hello-456");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-456");
                        assertThat(MDC.get("accountId")).isEqualTo("account-456");

                        // defaults too
                        assertThat(MDC.get("deliveryId")).isEqualTo("delivery-456");
                        assertThat(MDC.get("invocationId")).isEqualTo("invocation-456");
                        assertThat(MDC.get("retryNo")).isEqualTo("456");

                        syncLatch.countDown();
                        return Mono.just(new BotDialogMessageResponse());
                    }
                    return Mono.error(new RuntimeException("Unexpected event id"));
                },
                // checking context in async handler
                request -> {

                    if (Objects.equals(request.headers().getFirst("X-Unblu-Invocation-ID"), "invocation-123")) {
                        assertThat(MDC.get("text")).isEqualTo("hello-123");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-123");
                        assertThat(MDC.get("accountId")).isEqualTo("account-123");

                        // defaults too
                        assertThat(MDC.get("deliveryId")).isEqualTo("delivery-123");
                        assertThat(MDC.get("invocationId")).isEqualTo("invocation-123");
                        assertThat(MDC.get("retryNo")).isEqualTo("123");

                        asyncLatch.countDown();
                        return Mono.just("ok").then();
                    }

                    if (Objects.equals(request.headers().getFirst("X-Unblu-Invocation-ID"), "invocation-456")) {
                        assertThat(MDC.get("text")).isEqualTo("hello-456");
                        assertThat(MDC.get("conversationId")).isEqualTo("conv-456");
                        assertThat(MDC.get("accountId")).isEqualTo("account-456");

                        // defaults too
                        assertThat(MDC.get("deliveryId")).isEqualTo("delivery-456");
                        assertThat(MDC.get("invocationId")).isEqualTo("invocation-456");
                        assertThat(MDC.get("retryNo")).isEqualTo("456");

                        asyncLatch.countDown();
                        return Mono.just("ok").then();
                    }

                    return Mono.error(new RuntimeException("Unexpected event id"));
                },
                RequestOrderSpec.canIgnoreOrder(),
                ContextSpec.of(
                        "text", e -> e.body().getConversationMessage().getFallbackText(),
                        "conversationId", e -> e.body().getConversationMessage().getConversationId(),
                        "accountId", e -> e.body().getConversationMessage().getAccountId()
                ));

        outboundRequestHandler.assertSubscribed();

        send("outbound.bot.dialog.message", "invocation-123", "delivery-123", 123, new BotDialogMessageRequest()
                .conversationMessage(
                        new TextMessageData()
                                .fallbackText("hello-123")
                                .conversationId("conv-123")
                                .accountId("account-123")
                )
        );

        send("outbound.bot.dialog.message", "invocation-456", "delivery-456", 456, new BotDialogMessageRequest()
                .conversationMessage(
                        new TextMessageData()
                                .fallbackText("hello-456")
                                .conversationId("conv-456")
                                .accountId("account-456")
                )
        );

        assertTrue(syncLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        assertTrue(asyncLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @SneakyThrows
    private void send(String requestType, String invocationId, String deliveryId, long retryNo, Object body) {
        var bodySerialized = objectMapper.writeValueAsString(body);
        var signature = calculateSignature(bodySerialized);
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", requestType)
                .header("X-Unblu-Invocation-ID", invocationId)
                .header("X-Unblu-Retry-No", String.valueOf(retryNo))
                .header("X-Unblu-Delivery", deliveryId)
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodySerialized)
                .exchange()
                .expectStatus().isOk();
    }

    private String calculateSignature(Object body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, outboundRequestsConfiguration.getSecret()).hmacHex(body.toString().getBytes());
    }
}
