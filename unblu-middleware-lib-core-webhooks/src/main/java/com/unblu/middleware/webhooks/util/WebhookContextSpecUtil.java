package com.unblu.middleware.webhooks.util;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.utils.RequestWrapperUtils;
import com.unblu.middleware.webhooks.entity.WebhookHandlerOptions;

import java.net.http.HttpHeaders;

public class WebhookContextSpecUtil {

    public static ContextSpec<HttpHeaders> webhookHeadersContextSpec() {
        return ContextSpec.of(
                "eventId", headers -> headers.firstValue("X-Unblu-Event-ID").orElse(null),
                "deliveryId", headers -> headers.firstValue("X-Unblu-Delivery").orElse(null),
                "retryNo", headers -> headers.firstValue("X-Unblu-Retry-No").orElse(null)
        );
    }

    public static <T> WebhookHandlerOptions<Request<T>> wrapped(WebhookHandlerOptions<T> options) {
        return new WebhookHandlerOptions<>(
                RequestWrapperUtils.wrapped(options.requestOrderSpec()),
                RequestWrapperUtils.wrapped(options.contextSpec()),
                options.shouldAssertRegistered()
        );
    }
}
