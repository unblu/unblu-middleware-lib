package com.unblu.middleware.controller;

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

import static io.netty.util.ReferenceCountUtil.release;
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
        return DataBufferUtils
                .join(bodyBuffer)
                .switchIfEmpty(Mono.just(emptyBuffer(headers.getContentLength())))
                .map((ThrowingFunction<DataBuffer, byte[]>) dataBuffer -> {
                    try (InputStream bodyStream = dataBuffer.asInputStream()) {
                        return copyToByteArray(bodyStream);
                    } finally {
                        release(dataBuffer);
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
