package com.unblu.middleware.outboundrequests.util;

import com.unblu.middleware.common.entity.ContextSpec;
import org.springframework.http.HttpHeaders;

public class OutboundRequestsContextSpecUtil {
    public static ContextSpec<HttpHeaders> outboundRequestHeadersContextSpec() {
        return ContextSpec.of(
                "invocationId", headers -> headers.getFirst("X-Unblu-Invocation-ID"),
                "deliveryId", headers -> headers.getFirst("X-Unblu-Delivery"),
                "retryNo", headers -> headers.getFirst("X-Unblu-Retry-No")
        );
    }
}
