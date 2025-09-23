package com.unblu.middleware.webhooks.util;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;

public class WebhookContextSpecUtil {
    public static <T> ContextSpec<Request<T>> webhookContextSpec() {
        return ContextSpec.of(
                "eventId", req -> req.headers().getFirst("X-Unblu-Event-ID"),
                "deliveryId", req -> req.headers().getFirst("X-Unblu-Delivery"),
                "retryNo", req -> req.headers().getFirst("X-Unblu-Retry-No")
        );
    }
}
