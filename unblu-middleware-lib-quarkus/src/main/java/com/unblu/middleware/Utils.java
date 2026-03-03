package com.unblu.middleware;

import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.common.entity.RawRequest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class Utils {
    public static <T> Uni<T> monoToUni(Mono<T> mono) {
        return Uni.createFrom().completionStage(mono.toFuture());
    }

    public static @NonNull RawRequest toLibHttpRequest(byte[] body, HttpHeaders headers) {
        return new RawRequest(
                body,
                java.net.http.HttpHeaders.of(headers.getRequestHeaders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), (a, b) -> true));
    }

    public static Response libHttpResponseToResponseEntity(HttpResponse<String> it) {
        Response.ResponseBuilder builder = Response.status(it.status());
        it.headers().map().forEach((key, values) -> values.forEach(value -> builder.header(key, value)));
        if (it.body() == null) {
            return builder.build();
        }
        return builder.entity(it.body()).build();
    }
}
