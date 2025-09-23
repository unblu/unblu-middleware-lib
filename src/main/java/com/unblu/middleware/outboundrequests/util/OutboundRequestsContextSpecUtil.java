package com.unblu.middleware.outboundrequests.util;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;

public class OutboundRequestsContextSpecUtil {
    public static <T> ContextSpec<Request<T>> outboundRequestContextSpec() {
        return ContextSpec.of(
                "invocationId", req -> req.headers().getFirst("X-Unblu-Invocation-ID"),
                "deliveryId", req -> req.headers().getFirst("X-Unblu-Delivery"),
                "retryNo", req -> req.headers().getFirst("X-Unblu-Retry-No")
        );
    }
}
