package com.unblu.middleware;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import com.unblu.middleware.common.utils.ThrowingFunction;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StreamUtils.copyToByteArray;

@UtilityClass
public class Utils {

    private static final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    public @NonNull ResponseEntity<String> libHttpResponseToResponseEntity(HttpResponse<String> response) {
        return ResponseEntity
                .status(response.status())
                .headers(httpHeaders -> response.headers().map().forEach(httpHeaders::addAll))
                .body(response.body());
    }

    public @NonNull Mono<RawRequest> toLibHttpRequest(Flux<DataBuffer> bodyBuffer, HttpHeaders headers) {
        return toLibHttpRequest(bodyBuffer, headers, Integer.MAX_VALUE);
    }

    // maxBodyBytes: the raw Flux<DataBuffer> body bypasses spring.codec.max-in-memory-size,
    // so the join itself must be capped (errors with DataBufferLimitException when exceeded)
    public @NonNull Mono<RawRequest> toLibHttpRequest(Flux<DataBuffer> bodyBuffer, HttpHeaders headers, int maxBodyBytes) {
        return DataBufferUtils
                .join(bodyBuffer, maxBodyBytes)
                .switchIfEmpty(Mono.fromSupplier(() -> emptyBuffer(headers.getContentLength())))
                // covers buffers dropped on cancel/error before map() takes ownership
                .doOnDiscard(DataBuffer.class, DataBufferUtils::release)
                .map((ThrowingFunction<DataBuffer, byte[]>) dataBuffer -> {
                    try (InputStream bodyStream = dataBuffer.asInputStream()) {
                        return copyToByteArray(bodyStream);
                    } finally {
                        // must be DataBufferUtils.release: Netty's ReferenceCountUtil.release
                        // is a silent no-op on Spring DataBuffers (not ReferenceCounted)
                        DataBufferUtils.release(dataBuffer);
                    }
                })
                .map(body -> new RawRequest(body, toJavaNetHeaders(headers)));
    }

    private static java.net.http.HttpHeaders toJavaNetHeaders(HttpHeaders headers) {
        return java.net.http.HttpHeaders.of(headers.headerSet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), (s1, s2) -> true);
    }

    private DataBuffer emptyBuffer(long contentLength) {
        return dataBufferFactory.allocateBuffer(contentLength > 0 ? (int) contentLength : 256);
    }
}
