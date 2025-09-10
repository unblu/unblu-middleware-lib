package com.unblu.middleware.common.request;

import com.unblu.middleware.common.error.InvalidRequestException;
import com.unblu.middleware.common.error.NoHandlerException;
import com.unblu.middleware.common.utils.ThrowingFunction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.Optional;

import static org.springframework.core.io.buffer.DataBufferUtils.release;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.util.StreamUtils.copyToByteArray;

@Slf4j
public class RequestHandler {

    private final DataBufferFactory dataBufferFactory;
    private final HmacUtils hmacSha1;
    private final HmacUtils hmacSha256;

    public RequestHandler(DataBufferFactory dataBufferFactory, RequestHandlerConfiguration requestHandlerConfiguration) {
        this.dataBufferFactory = dataBufferFactory;
        this.hmacSha1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, requestHandlerConfiguration.secretKey());
        this.hmacSha256 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, requestHandlerConfiguration.secretKey());
    }


    public Mono<ResponseEntity<Object>> handle(@NonNull ServerHttpRequest request,
                                               @NonNull ThrowingFunction<byte[], Mono<ResponseEntity<Object>>> processAction) {

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
                .onErrorResume(InvalidRequestException.class, e -> {
                    log.error(withRequestContext("Request not valid: {}", request.getHeaders()), e.getMessage(), e);
                    return Mono.just(badRequest().body("Request not valid: " + e.getMessage()));
                })
                .onErrorResume(NoHandlerException.class, e -> {
                    log.error(withRequestContext("No handler registered for request: {}", request.getHeaders()), e.getMessage(), e);
                    return Mono.just(badRequest().body("No handler registered for request"));
                })
                .onErrorResume(e -> {
                    log.error(withRequestContext("Error while processing request: {}", request.getHeaders()), e.getMessage(), e);
                    return Mono.just(internalServerError().body("Error while processing request: " + e.getMessage()));
                });
    }

    private DataBuffer emptyBuffer(long contentLength) {
        return dataBufferFactory.allocateBuffer(contentLength > 0 ? (int) contentLength : 256);
    }

    private void checkHeaders(HttpHeaders headers) {
        var userAgent = headers.getFirst("user-agent");
        if (!"Unblu-Hookshot".equals(userAgent)) {
            throw new InvalidRequestException(withRequestContext("Dropping request due to wrong useragent: " + userAgent, headers));
        }
    }

    private void checkSignature(HttpHeaders headers, byte[] body) {
        var receivedSignature = Optional.ofNullable(headers.getFirst("x-unblu-signature"));
        var receivedSignature256 = Optional.ofNullable(headers.getFirst("x-unblu-signature-256"));

        if (receivedSignature.isEmpty() && receivedSignature256.isEmpty()) {
            throw new InvalidRequestException(withRequestContext("Webhook signature not present", headers));
        }

        receivedSignature256.ifPresent(it -> {
            var calculatedSignature = hmacSha256.hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException(withRequestContext("Webhook signature mismatch for SHA256", headers));
            }
        });

        receivedSignature.ifPresent(it -> {
            var calculatedSignature = hmacSha1.hmacHex(body);
            if (!it.equals(calculatedSignature)) {
                throw new InvalidRequestException(withRequestContext("Webhook signature mismatch for SHA1", headers));
            }
        });
    }

    public static String withRequestContext(String message, HttpRequest headers) {
        return withRequestContext(message, headers.getHeaders());
    }

    public static String withRequestContext(String message, HttpHeaders headers) {
        var deliveryIdInfo = Optional.ofNullable(headers.getFirst("X-Unblu-Delivery"))
                .map(it -> ", Delivery id: " + it)
                .orElse("");

        var invocationIdInfo = Optional.ofNullable(headers.getFirst("X-Unblu-Invocation"))
                .map(it -> ", Invocation id: " + it)
                .orElse("");

        var eventIdInfo = Optional.ofNullable(headers.getFirst("X-Unblu-Event-ID"))
                .map(it -> ", Event id: " + it)
                .orElse("");

        var retryNoInfo = Optional.ofNullable(headers.getFirst("X-Unblu-Retry-No"))
                .map(it -> ", Retry no: " + it)
                .orElse("");

        return message +
                deliveryIdInfo +
                invocationIdInfo +
                eventIdInfo +
                retryNoInfo;
    }
}
