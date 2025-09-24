package com.unblu.middleware;

import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@DirtiesContext
class BotsControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Test
    void onMissingUserAgent_resultIsBadRequest() {
        var body = "{\"type\":\"outbound.request\"}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/outbound")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .header("X-Unblu-Signature", signature)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onPing_resultIsPongOk() {
        var body = "{}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.ping")
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Pong!");
    }

    @Test
    void onCorrectDialogOpened_resultIsOk() {
        var body = "{\"$_type\":\"outbound.bot.dialog.opened\"}";
        var signature = calculateSignature(body);
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.bot.dialog.opened")
                .header("X-Unblu-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void onWrongSignature_resultIsBadRequest() {
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "test")
                .header("X-Unblu-Signature", "muhaha")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"outbound.request\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onMissingSignature_resultIsBadRequest() {
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"outbound.request\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onMissingServiceName_resultIsBadRequest() {
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"outbound.request\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void onEmptyServiceName_resultIsBadRequest() {
        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"type\":\"outbound.request\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    private String calculateSignature(Object body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, outboundRequestsConfiguration.getSecret()).hmacHex(body.toString().getBytes());
    }
}
