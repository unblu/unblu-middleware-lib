package com.unblu.middleware.webhooks.util;

import com.unblu.middleware.common.entity.ContextSpec;

import java.net.http.HttpHeaders;

public class WebhookContextSpecUtil {

    public static ContextSpec<HttpHeaders> webhookHeadersContextSpec() {
        return ContextSpec.of(
                "eventId", headers -> headers.firstValue("X-Unblu-Event-ID").orElse(null),
                "deliveryId", headers -> headers.firstValue("X-Unblu-Delivery").orElse(null),
                "retryNo", headers -> headers.firstValue("X-Unblu-Retry-No").orElse(null)
        );
    }
}
