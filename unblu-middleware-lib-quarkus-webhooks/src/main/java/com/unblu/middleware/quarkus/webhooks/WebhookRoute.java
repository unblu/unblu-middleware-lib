package com.unblu.middleware.quarkus.webhooks;

import com.unblu.middleware.Utils;
import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.controller.WebhookControllerService;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.unblu.middleware.Utils.toLibHttpRequest;

/**
 * Quarkus route registration for handling Unblu webhooks.
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class WebhookRoute {

    private final Router router;
    private final WebhookConfiguration webhookConfiguration;
    private final WebhookControllerService webhookControllerService;

    void register(@Observes StartupEvent _event) {
        var path = normalizedPath(webhookConfiguration.apiPath());
        log.info("Registering webhook route at {}", path);
        router.post(path).handler(this::handleWebhook);
    }

    private void handleWebhook(RoutingContext context) {
        var request = context.request();
        var xUnbluEvent = request.getHeader("x-unblu-event");
        request.exceptionHandler(context::fail);
        request.bodyHandler(body -> Utils.monoToUni(
                        webhookControllerService.webhook(xUnbluEvent, toLibHttpRequest(body == null ? new byte[0] : body.getBytes(), context.request().headers())))
                .subscribe()
                .with(
                        response -> writeResponse(context, response),
                        context::fail
                ));
        request.resume();
    }

    private static void writeResponse(RoutingContext context, HttpResponse<String> response) {
        var vertxResponse = context.response().setStatusCode(response.status());
        response.headers().map().forEach((name, values) -> values.forEach(value -> vertxResponse.putHeader(name, value)));
        if (response.body() == null) {
            vertxResponse.end();
            return;
        }
        vertxResponse.end(response.body());
    }

    private static String normalizedPath(String configuredPath) {
        var trimmed = configuredPath == null ? "" : configuredPath.trim();
        if (trimmed.isEmpty()) {
            return "/";
        }
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
