package com.unblu.middleware.common.entity;

import java.net.http.HttpHeaders;

public record Request<T>(
        T body,
        HttpHeaders headers
) {
}
