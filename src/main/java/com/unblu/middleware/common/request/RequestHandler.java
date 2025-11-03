package com.unblu.middleware.common.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.utils.ThrowingFunction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.springframework.core.io.buffer.DataBufferUtils.release;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.util.StreamUtils.copyToByteArray;

@Slf4j
public class RequestHandler {

    private final ObjectMapper objectMapper;
    private final DataBufferFactory dataBufferFactory;
    private final HmacUtils hmacSha1;
    private final HmacUtils hmacSha256;
    private final ContextSpec<HttpHeaders> contextSpec;

    public RequestHandler(DataBufferFactory dataBufferFactory, RequestHandlerConfiguration requestHandlerConfiguration, ContextRegistryWrapper contextRegistryWrapper, ObjectMapper objectMapper, ContextSpec<HttpHeaders> contextSpec) {
        this.dataBufferFactory = dataBufferFactory;
        this.objectMapper = objectMapper;
        this.contextSpec = contextSpec;
        contextRegistryWrapper.registerContextSpec(contextSpec);
        this.hmacSha1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, requestHandlerConfiguration.secretKey());
        this.hmacSha256 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, requestHandlerConfiguration.secretKey());
    }


    public <T> Mono<ResponseEntity<String>> handle(@NonNull ServerHttpRequest request,
                                                   @NonNull ThrowingFunction<byte[], Mono<T>> processAction
    ) {

        long contentLength = request.getHeaders().getContentLength();
        return DataBufferUtils
                .join(request.getBody())
                .switchIfEmpty(Mono.just(emptyBuffer(contentLength)))
                .map((ThrowingFunction<DataBuffer, byte[]>) dataBuffer -> {
                    try (InputStream bodyStream = dataBuffer.asInputStream()) {
                        var body = copyToByteArray(bodyStream);
                        checkHeaders(request.getHeaders());
                        checkSignature(request.getHeaders(), body);
                        return body;
                    } finally {
                        release(dataBuffer);
                    }
                })
                .flatMap(processAction)
                .flatMap(this::response)
                .onErrorResume(InvalidRequestException.class, e -> {
                    log.error("Request not valid: {}", e.getMessage());
                    return Mono.just(badRequest().body("Request not valid: " + e.getMessage()));
                })
                .onErrorResume(NoHandlerException.class, e -> {
                    log.error("No handler registered for request: {}", e.getMessage());
                    return Mono.just(badRequest().body("No handler registered for request"));
                })
                .onErrorResume(e -> {
                    log.error("Error while processing request: {}", e.getMessage());
                    return Mono.just(internalServerError().body("Error while processing request: " + e.getMessage()));
                })
                .map(this::signed)
                .contextWrite(contextSpec.applyTo(request.getHeaders()));
    }

    private DataBuffer emptyBuffer(long contentLength) {
        return dataBufferFactory.allocateBuffer(contentLength > 0 ? (int) contentLength : 256);
    }

    private void checkHeaders(HttpHeaders headers) {
        var userAgent = headers.getFirst("user-agent");
        if (!"Unblu-Hookshot".equals(userAgent)) {
            throw new InvalidRequestException("Dropping request due to wrong useragent: " + userAgent);
        }
    }

    private void checkSignature(HttpHeaders headers, byte[] body) {
        var receivedSignature = Optional.ofNullable(headers.getFirst("x-unblu-signature"));
        var receivedSignature256 = Optional.ofNullable(headers.getFirst("x-unblu-signature-256"));

        if (receivedSignature.isEmpty() && receivedSignature256.isEmpty()) {
            throw new InvalidRequestException("Webhook signature not present");
        }

        receivedSignature256.ifPresent(it -> {
            var calculatedSignature = hmacSha256.hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException("Webhook signature mismatch for SHA256");
            }
        });

        receivedSignature.ifPresent(it -> {
            var calculatedSignature = hmacSha1.hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException("Webhook signature mismatch for SHA1");
            }
        });
    }

    private <T> Mono<ResponseEntity<String>> response(T body) {
        return Mono.just(body)
                .map((ThrowingFunction<T, String>) objectMapper::writeValueAsString)
                .map(bodySerialized -> ok()
                        .contentType(APPLICATION_JSON)
                        .body(bodySerialized));
    }

    private ResponseEntity<String> signed(ResponseEntity<String> responseEntity) {
        return ResponseEntity
                .status(responseEntity.getStatusCode())
                .headers(httpHeaders -> {
                    httpHeaders.putAll(responseEntity.getHeaders());
                    httpHeaders.put("x-unblu-signature-256", List.of(hmacSha256.hmacHex(responseEntity.getBody())));
                })
                .body(responseEntity.getBody());
    }
}
