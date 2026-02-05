package com.unblu.middleware.common.entity;

import java.net.http.HttpHeaders;

public record RawRequest(
        byte[] body,
        HttpHeaders headers
) {
}
