package com.unblu.middleware.common.entity;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record HttpResponse<T>(
        int status,
        HttpHeaders headers,
        T body
) {
    public static <T> HttpResponse<T> ok(T body) {
        return new HttpResponse<>(200, emptyHeaders(), body);
    }

    public static <T> HttpResponse<T> badRequest(T body) {
        return new HttpResponse<>(400, emptyHeaders(), body);
    }

    public static <T> HttpResponse<T> internalServerError(T body) {
        return new HttpResponse<>(500, emptyHeaders(), body);
    }

    private static HttpHeaders emptyHeaders() {
        return HttpHeaders.of(Map.of(), (k, v) -> true);
    }

    public HttpResponse<T> contentType(String contentType) {
        return withHeader("Content-Type", contentType);
    }

    public HttpResponse<T> withHeader(String headerName, String headerValue) {
        var headersMap = new HashMap<>(headers.map());
        headersMap.put(headerName, List.of(headerValue));
        return new HttpResponse<>(this.status, HttpHeaders.of(headersMap, (k, v) -> true), this.body);
    }
}

