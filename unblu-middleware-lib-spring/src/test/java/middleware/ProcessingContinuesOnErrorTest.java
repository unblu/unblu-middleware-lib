package middleware;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.service.WebhookHandler;
import com.unblu.webapi.model.v4.BotDialogMessageRequest;
import com.unblu.webapi.model.v4.BotDialogMessageResponse;
import com.unblu.webapi.model.v4.ConversationNewMessageEvent;
import com.unblu.webapi.model.v4.TextMessageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CountDownLatch;

import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;
import static com.unblu.middleware.webhooks.entity.EventName.eventName;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
class ProcessingContinuesOnErrorTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    OutboundRequestsConfiguration outboundRequestsConfiguration;

    @Autowired
    WebhookConfiguration webhookConfiguration;

    @Autowired
    OutboundRequestHandler outboundRequestHandler;

    @Autowired
    WebhookHandler webhookHandler;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void givenPreviousError_onNextWebhook_processingContinues() throws InterruptedException {
        CountDownLatch errorLatch = new CountDownLatch(1);
        CountDownLatch okLatch = new CountDownLatch(1);

        webhookHandler.onWebhook(eventName("conversation.new_message"), ConversationNewMessageEvent.class,
                request -> {
                    if ("error".equals(request.getConversationMessage().getFallbackText())) {
                        errorLatch.countDown();
                        return Mono.error(new RuntimeException("Simulated processing error"));
                    } else {
                        okLatch.countDown();
                        return Mono.just("ok").then();
                    }
                });

        webhookHandler.assertSubscribed();

        var firstMessage = serialize(new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData().fallbackText("error")));

        var secondMessage = serialize(new ConversationNewMessageEvent()
                .conversationMessage(new TextMessageData().fallbackText("all good")));

        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "conversation.new_message")
                .header("X-Unblu-Signature", signedWebhook(firstMessage))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(firstMessage)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/webhook")
                .header("User-Agent", "Unblu-Hookshot")
                .header("X-Unblu-Event", "conversation.new_message")
                .header("X-Unblu-Signature", signedWebhook(secondMessage))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(secondMessage)
                .exchange()
                .expectStatus().isOk();

        assertTrue(errorLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        assertTrue(okLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    void givenPreviousError_onNextOutboundRequest_processingContinues() throws InterruptedException {
        CountDownLatch errorLatch = new CountDownLatch(1);
        CountDownLatch okLatch = new CountDownLatch(1);

        outboundRequestHandler.on(outboundRequestType("outbound.bot.dialog.message"), BotDialogMessageRequest.class, BotDialogMessageResponse.class, _r -> Mono.just(new BotDialogMessageResponse()),
                request -> {
                    if ("error".equals(request.getConversationMessage().getFallbackText())) {
                        errorLatch.countDown();
                        return Mono.error(new RuntimeException("Simulated processing error"));
                    } else {
                        okLatch.countDown();
                        return Mono.just("ok").then();
                    }
                }, RequestOrderSpec.canIgnoreOrder(), ContextSpec.empty());

        outboundRequestHandler.assertSubscribed();

        var firstMessage = serialize(new BotDialogMessageRequest()
                .conversationMessage(new TextMessageData().fallbackText("error")));

        var secondMessage = serialize(new BotDialogMessageRequest()
                .conversationMessage(new TextMessageData().fallbackText("all good")));

        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.bot.dialog.message")
                .header("X-Unblu-Signature", signedOutbound(firstMessage))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(firstMessage)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/outbound")
                .header("User-Agent", "Unblu-Hookshot")
                .header("x-unblu-service-name", "outbound.bot.dialog.message")
                .header("X-Unblu-Signature", signedOutbound(secondMessage))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(secondMessage)
                .exchange()
                .expectStatus().isOk();

        assertTrue(errorLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        assertTrue(okLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    private String serialize(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    private String signedWebhook(String body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webhookConfiguration.getSecret()).hmacHex(body.getBytes());
    }

    private String signedOutbound(String body) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, outboundRequestsConfiguration.getSecret()).hmacHex(body.getBytes());
    }
}
