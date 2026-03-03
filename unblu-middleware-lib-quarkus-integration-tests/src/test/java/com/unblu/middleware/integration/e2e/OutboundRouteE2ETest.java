package com.unblu.middleware.integration.e2e;

import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(OutboundRouteE2ETest.Profile.class)
class OutboundRouteE2ETest {

    @Inject
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Test
    void onMissingUserAgent_resultIsBadRequest() {
        String body = "{\"type\":\"outbound.request\"}";

        given()
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post("/outbound")
                .then()
                .statusCode(400);
    }

    @Test
    void onPing_resultIsOk() {
        String body = "{}";

        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.ping")
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post("/outbound")
                .then()
                .statusCode(200);
    }

    @Test
    void onWrongSignature_resultIsBadRequest() {
        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.bot.dialog.opened")
                .header("X-Unblu-Signature", "muhaha")
                .contentType("application/json")
                .body("{\"type\":\"outbound.request\"}")
                .when()
                .post("/outbound")
                .then()
                .statusCode(400);
    }

    @Test
    void onMissingServiceName_resultIsBadRequest() {
        String body = "{\"type\":\"outbound.request\"}";

        given()
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Signature", sign(body))
                .contentType("application/json")
                .body(body)
                .when()
                .post("/outbound")
                .then()
                .statusCode(400);
    }

    private String sign(String body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, outboundRequestsConfiguration.getSecret())
                .hmacHex(body.getBytes());
    }

    public static class Profile implements QuarkusTestProfile {
    }
}
