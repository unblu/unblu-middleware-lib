package com.unblu.middleware;

import com.unblu.middleware.webhooks.config.WebhookRegistrationConfiguration;
import com.unblu.middleware.webhooks.service.WebhookHandlerService;
import com.unblu.middleware.webhooks.service.WebhookRegistrationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.unblu.middleware.common.registry.RequestOrderSpec.canIgnoreOrder;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class WebhookControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    WebhookRegistrationConfiguration webhookRegistrationConfiguration;

    @Autowired
    WebhookHandlerService webhookHandlerService;

    @MockitoBean
    WebhookRegistrationService webhookRegistrationService;

    @PostConstruct
    public void init() {
        when(webhookRegistrationService.isRegisteredFor(eventName("test"))).thenReturn(true);
        webhookHandlerService.onWebhook(eventName("test"), Object.class, event -> Mono.fromRunnable(() -> log.info("Handled test webhook")), canIgnoreOrder());
        webhookHandlerService.subscribe();
    }

    @Test
    void onMissingUserAgent_resultIsBadRequest() {
        var body = "{\"type\":\"event\"}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .header("X-Unblu-Signature", signature)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onPing_resultIsOkPong() {
        var body = "{}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "ping")
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Pong!");
    }

    @Test
    void givenTestEventHandlerRegistered_onTestEvent_resultIsOk() {
        var body = "{\"type\":\"event\"}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "test")
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void onWrongSignature_resultIsBadRequest() {
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "test")
                .header("X-Unblu-Signature", "muhaha")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onMissingSignature_resultIsBadRequest() {
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onMissingEventHeader_resultIsBadRequest() {
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onEmptyEventHeader_resultIsBadRequest() {
        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    private String calculateSignature(Object body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webhookRegistrationConfiguration.getSecret()).hmacHex(body.toString().getBytes());
    }
}
