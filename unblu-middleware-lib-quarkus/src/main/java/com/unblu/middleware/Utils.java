package com.unblu.middleware;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import lombok.experimental.UtilityClass;
import org.jboss.resteasy.reactive.RestResponse;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class Utils {
    public static <T> Uni<T> monoToUni(Mono<T> mono) {
        return Uni.createFrom().completionStage(mono.toFuture());
    }

    public static @NonNull RawRequest toLibHttpRequest(Buffer body, HttpHeaders headers) {
        return new RawRequest(
                body.getBytes(),
                java.net.http.HttpHeaders.of(headers.getRequestHeaders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), (a, b) -> true));
    }

    public static RestResponse<Object> libHttpResponseToResponseEntity(HttpResponse<String> it) {
        var headersMap = new MultivaluedHashMap<String, Object>();
        it.headers().map().forEach((key, values) ->
                values.forEach(v -> headersMap.add(key, v))
        );
        return RestResponse.ResponseBuilder.create(it.status())
                .replaceAll(headersMap)
                .status(it.status(), it.body())
                .build();
    }
}
