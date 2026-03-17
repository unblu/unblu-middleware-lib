package com.unblu.middleware.integration.e2e;

import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.service.WebhookHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static io.restassured.RestAssured.given;
import static reactor.core.publisher.Mono.empty;

@QuarkusTest
@TestProfile(WebhookRouteE2ETest.Profile.class)
class WebhookRouteE2ETest {

    @Inject
    WebhookConfiguration webhookConfiguration;

    @Inject
    WebhookHandler webhookHandler;

    @BeforeEach
    void setupHandler() {
        webhookHandler.onWebhook(eventName("test.e2e.webhook"), Object.class, _event -> empty());
        webhookHandler.assertSubscribed();
    }

    @Test
    void onMissingUserAgent_resultIsBadRequest() {
        String body = "{\"type\":\"event\"}";

        given()
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post(webhookConfiguration.apiPath())
                .then()
                .statusCode(400);
    }

    @Test
    void onPing_resultIsOk() {
        String body = "{}";

        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "ping")
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post(webhookConfiguration.apiPath())
                .then()
                .statusCode(200);
    }

    @Test
    void givenHandlerRegistered_onCustomEvent_resultIsOk() {
        String body = "{\"type\":\"event\"}";

        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "test.e2e.webhook")
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post(webhookConfiguration.apiPath())
                .then()
                .statusCode(200);
    }

    @Test
    void onWrongSignature_resultIsBadRequest() {
        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "test.e2e.webhook")
                .header("X-Unblu-Signature", "muhaha")
                .contentType("application/json")
                .body("{\"type\":\"event\"}")
                .when()
                .post(webhookConfiguration.apiPath())
                .then()
                .statusCode(400);
    }

    @Test
    void onMissingEventHeader_resultIsBadRequest() {
        String body = "{\"type\":\"event\"}";

        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post(webhookConfiguration.apiPath())
                .then()
                .statusCode(400);
    }

    private String sign(String body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webhookConfiguration.secret())
                .hmacHex(body.getBytes());
    }

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("unblu.webhook.api-path", "/custom-webhook");
        }
    }
}
