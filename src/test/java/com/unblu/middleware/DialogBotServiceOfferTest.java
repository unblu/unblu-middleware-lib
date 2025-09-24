package com.unblu.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.bots.service.DialogBotService;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.webapi.model.v4.BotOffboardingOfferRequest;
import com.unblu.webapi.model.v4.BotOnboardingOfferRequest;
import com.unblu.webapi.model.v4.BotReboardingOfferRequest;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
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
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
@DirtiesContext
class DialogBotServiceOfferTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Autowired
    DialogBotService dialogBotService;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        dialogBotService.acceptOnboardingOfferIf(o -> Mono.just("testOnAccountId".equals(o.getAccountId())));
        dialogBotService.acceptReboardingOfferIf(o -> Mono.just("testReAccountId".equals(o.getAccountId())));
        dialogBotService.acceptOffboardingOfferIf(o -> Mono.just("testOffAccountId".equals(o.getAccountId())));
    }

    @Test
    void givenAcceptingOffersOnlyForTestOnAccount_onOnboardingOfferFromTestOnAccount_resultIsOkAccepted() {
        outBoundRequest("outbound.bot.onboarding_offer",
                new BotOnboardingOfferRequest()
                        .accountId("testOnAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(true);
    }

    @Test
    void givenAcceptingOffersOnlyForTestAccount_onOnboardingOfferFromRandomAccount_resultIsOkNotAccepted() {
        outBoundRequest("outbound.bot.onboarding_offer",
                new BotOnboardingOfferRequest()
                        .accountId("testReAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(false);
    }

    @Test
    void givenAcceptingOffersOnlyForTestReAccount_onReboardingOfferFromTestReAccount_resultIsOkAccepted() {
        outBoundRequest("outbound.bot.reboarding_offer",
                new BotReboardingOfferRequest()
                        .accountId("testReAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(true);
    }

    @Test
    void givenAcceptingOffersOnlyForTestReAccount_onReboardingOfferFromRandomAccount_resultIsOkNotAccepted() {
        outBoundRequest("outbound.bot.reboarding_offer",
                new BotReboardingOfferRequest()
                        .accountId("testOnAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(false);
    }

    @Test
    void givenAcceptingOffersOnlyForTestOffAccount_onOffboardingOfferFromTestOffAccount_resultIsOkAccepted() {
        outBoundRequest("outbound.bot.offboarding_offer",
                new BotOffboardingOfferRequest()
                        .accountId("testOffAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(true);
    }

    @Test
    void givenAcceptingOffersOnlyForTestOffAccount_onOffboardingOfferFromRandomAccount_resultIsOkNotAccepted() {
        outBoundRequest("outbound.bot.offboarding_offer",
                new BotOffboardingOfferRequest()
                        .accountId("testReAccountId")
        )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.$_type").isEqualTo("BotBoardingOfferResponse")
                .jsonPath("$.offerAccepted").isEqualTo(false);
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
