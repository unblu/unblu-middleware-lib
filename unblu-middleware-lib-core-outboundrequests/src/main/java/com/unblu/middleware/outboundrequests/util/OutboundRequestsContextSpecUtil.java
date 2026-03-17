package com.unblu.middleware.outboundrequests.util;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.utils.RequestWrapperUtils;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;

import java.net.http.HttpHeaders;

public class OutboundRequestsContextSpecUtil {
    public static ContextSpec<HttpHeaders> outboundRequestHeadersContextSpec() {
        return ContextSpec.of(
                "invocationId", headers -> headers.firstValue("X-Unblu-Invocation-ID").orElse(null),
                "deliveryId", headers -> headers.firstValue("X-Unblu-Delivery").orElse(null),
                "retryNo", headers -> headers.firstValue("X-Unblu-Retry-No").orElse(null)
        );
    }

    public static <T> OutboundRequestHandlerOptions<Request<T>> wrapped(OutboundRequestHandlerOptions<T> options) {
        return new OutboundRequestHandlerOptions<>(
                RequestWrapperUtils.wrapped(options.requestOrderSpec()),
                RequestWrapperUtils.wrapped(options.contextSpec())
        );
    }
}
