package com.unblu.middleware.common.entity;

import org.springframework.http.HttpHeaders;

public record Request<T>(
        T body,
        HttpHeaders headers
) {
}
