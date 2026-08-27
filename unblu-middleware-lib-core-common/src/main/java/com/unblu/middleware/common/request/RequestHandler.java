package com.unblu.middleware.common.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.utils.ThrowingFunction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
public class RequestHandler {

    private final ObjectMapper objectMapper;
    private final String secretKey;
    private final ContextSpec<HttpHeaders> contextSpec;

    public RequestHandler(RequestHandlerConfiguration requestHandlerConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper, ContextSpec<HttpHeaders> contextSpec) {
        this.objectMapper = objectMapper;
        this.contextSpec = contextSpec;
        contextRegistryWrapper.registerContextSpec(contextSpec);
        this.secretKey = requestHandlerConfiguration.secretKey();
    }

    // HmacUtils wraps a stateful javax.crypto.Mac, so a shared instance corrupts under
    // concurrent requests — construct per use
    private HmacUtils hmacSha1() {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey);
    }

    private HmacUtils hmacSha256() {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
    }


    public <T> Mono<HttpResponse<String>> handle(@NonNull RawRequest request,
                                                 @NonNull ThrowingFunction<RawRequest, Mono<T>> processAction
    ) {
        return Mono.just(request)
                .doOnNext(this::checkRequest)
                .flatMap(processAction)
                .flatMap(this::okResponse)
                .onErrorResume(InvalidRequestException.class, e -> {
                    log.error("Request not valid: {}", e.getMessage());
                    return Mono.just(HttpResponse.badRequest("Request not valid: " + e.getMessage()));
                })
                .onErrorResume(NoHandlerException.class, e -> {
                    log.error("No handler registered for request: {}", e.getMessage());
                    return Mono.just(HttpResponse.badRequest("No handler registered for request"));
                })
                .onErrorResume(e -> {
                    log.error("Error while processing request: {}", e.getMessage(), e);
                    return Mono.just(HttpResponse.internalServerError("Error while processing request: " + e.getMessage()));
                })
                .map(this::signed)
                .contextWrite(contextSpec.applyTo(request.headers()));
    }

    private void checkRequest(RawRequest request) {
        checkHeaders(request.headers());
        checkSignature(request.headers(), request.body());
    }

    private void checkHeaders(HttpHeaders headers) {
        var userAgent = headers.firstValue("user-agent")
                .orElseThrow(() -> new InvalidRequestException("User-Agent header not present"));
        if (!"Unblu-Hookshot".equals(userAgent)) {
            throw new InvalidRequestException("Dropping request due to wrong useragent: " + userAgent);
        }
    }

    private void checkSignature(HttpHeaders headers, byte[] body) {
        var receivedSignature = headers.firstValue("x-unblu-signature");
        var receivedSignature256 = headers.firstValue("x-unblu-signature-256");

        if (receivedSignature.isEmpty() && receivedSignature256.isEmpty()) {
            throw new InvalidRequestException("Webhook signature not present");
        }

        receivedSignature256.ifPresent(it -> {
            var calculatedSignature = hmacSha256().hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException("Webhook signature mismatch for SHA256");
            }
        });

        receivedSignature.ifPresent(it -> {
            var calculatedSignature = hmacSha1().hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException("Webhook signature mismatch for SHA1");
            }
        });
    }

    private <T> Mono<HttpResponse<String>> okResponse(T body) {
        return Mono.just(body)
                .map((ThrowingFunction<T, String>) objectMapper::writeValueAsString)
                .map(bodySerialized -> HttpResponse.ok(bodySerialized)
                        .contentType(APPLICATION_JSON));
    }

    private HttpResponse<String> signed(HttpResponse<String> responseEntity) {
        return responseEntity.withHeader("x-unblu-signature-256", hmacSha256().hmacHex(responseEntity.body()));
    }
}
