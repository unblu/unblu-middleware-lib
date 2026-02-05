package com.unblu.middleware.outboundrequests.util;

import com.unblu.middleware.common.entity.ContextSpec;

import java.net.http.HttpHeaders;

public class OutboundRequestsContextSpecUtil {
    public static ContextSpec<HttpHeaders> outboundRequestHeadersContextSpec() {
        return ContextSpec.of(
                "invocationId", headers -> headers.firstValue("X-Unblu-Invocation-ID").orElse(null),
                "deliveryId", headers -> headers.firstValue("X-Unblu-Delivery").orElse(null),
                "retryNo", headers -> headers.firstValue("X-Unblu-Retry-No").orElse(null)
        );
    }
}
