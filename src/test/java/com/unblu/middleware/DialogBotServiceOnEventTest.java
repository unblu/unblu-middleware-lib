package com.unblu.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.bots.service.DialogBotService;
import com.unblu.middleware.common.utils.ThrowingRunnable;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.webapi.model.v4.*;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class DialogBotServiceOnEventTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Autowired
    DialogBotService dialogBotService;

    @Autowired
    ObjectMapper objectMapper;

    private final Queue<String> testQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        dialogBotService.onDialogMessage(dialogMessage -> Mono.fromRunnable((ThrowingRunnable) () -> {
            Thread.sleep(200); // Simulate some processing delay
            log.info("Message Token: {} Thread: {}", dialogMessage.getDialogToken(), Thread.currentThread().getName());
            testQueue.add(dialogMessage.getConversationMessage().getFallbackText());
        }));

        dialogBotService.onDialogClosed(dialogClosed -> Mono.fromRunnable((ThrowingRunnable) () -> {
            Thread.sleep(100); // Simulate some processing delay
            log.info("Closed Token: {} Thread: {}", dialogClosed.getDialogToken(), Thread.currentThread().getName());
            testQueue.add(dialogClosed.getAccountId());
        }));

        // simulate dialog open event processed in parallel
        dialogBotService.onDialogOpen(dialogOpen -> Mono.fromRunnable((ThrowingRunnable) () -> {
            Thread.sleep(100);
            log.info("Open Token: {} Thread: {}", dialogOpen.getDialogToken(), Thread.currentThread().getName());
            testQueue.add(dialogOpen.getServiceName());
            Thread.sleep(1000); // Simulate some processing delay
            testQueue.add(dialogOpen.getAccountId());
        }).publishOn(Schedulers.parallel()).then());

        dialogBotService.subscribe();
    }

    @Test
    void givenSubscriptionsWithProcessingDelay_onRequests_processingOrderIsPreserved() {
        testQueue.clear();

        outBoundRequest("outbound.bot.dialog.opened",
                new BotDialogOpenRequest()
                        .dialogToken("dialog1")
                        .serviceName("One")
                        .accountId("Five")
        )
                .exchange()
                .expectStatus().isOk();

        outBoundRequest("outbound.bot.dialog.message",
                new BotDialogMessageRequest()
                        .dialogToken("dialog1")
                        .conversationMessage(new TextMessageData()
                                .fallbackText("Two"))
        )
                .exchange()
                .expectStatus().isOk();

        outBoundRequest("outbound.bot.dialog.message",
                new BotDialogMessageRequest()
                        .dialogToken("dialog1")
                        .conversationMessage(new TextMessageData()
                                .fallbackText("Three"))
        )
                .exchange()
                .expectStatus().isOk();

        outBoundRequest("outbound.bot.dialog.closed",
                new BotDialogClosedRequest()
                        .dialogToken("dialog1")
                        .accountId("Four")
        )
                .exchange()
                .expectStatus().isOk();

        testQueue.add("Zero");

        await().atMost(5, SECONDS)
                .until(() -> testQueue.size() >= 6);

        assertThat(testQueue).containsExactly("Zero", "One", "Two", "Three", "Four", "Five");
    }

    @SneakyThrows
    private WebTestClient.RequestHeadersSpec<?> outBoundRequest(String requestType, Object body) {
        var bodySerialized = objectMapper.writeValueAsString(body);
        var signature = calculateSignature(bodySerialized);
        return webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", requestType)
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodySerialized);
    }

    private String calculateSignature(Object body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, outboundRequestsConfiguration.getSecret()).hmacHex(body.toString().getBytes());
    }
}
