package com.unblu.middleware.webhooks.util;

import com.unblu.middleware.common.entity.ContextSpec;
import org.springframework.http.HttpHeaders;

public class WebhookContextSpecUtil {

    public static ContextSpec<HttpHeaders> webhookHeadersContextSpec() {
        return ContextSpec.of(
                "eventId", headers -> headers.getFirst("X-Unblu-Event-ID"),
                "deliveryId", headers -> headers.getFirst("X-Unblu-Delivery"),
                "retryNo", headers -> headers.getFirst("X-Unblu-Retry-No")
        );
    }
}
