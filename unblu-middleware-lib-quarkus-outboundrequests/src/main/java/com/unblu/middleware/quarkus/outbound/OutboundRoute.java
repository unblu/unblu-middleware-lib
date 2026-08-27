package com.unblu.middleware.quarkus.outbound;

import com.unblu.middleware.Utils;
import com.unblu.middleware.common.entity.HttpResponse;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.unblu.middleware.Utils.toLibHttpRequest;

/**
 * Quarkus route registration for handling Unblu outbound requests.
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class OutboundRoute {

    private final Router router;
    private final OutboundRequestsConfiguration outboundRequestsConfiguration;
    private final OutboundRequestsControllerService outboundRequestsControllerService;

    void register(@Observes StartupEvent _event) {
        var path = normalizedPath(outboundRequestsConfiguration.apiPath());
        log.info("Registering outbound route at {}", path);
        router.post(path).handler(this::handleOutbound);
    }

    private void handleOutbound(RoutingContext context) {
        var request = context.request();
        var xUnbluServiceName = request.getHeader("x-unblu-service-name");
        request.exceptionHandler(context::fail);
        request.bodyHandler(body -> Utils.monoToUni(
                        outboundRequestsControllerService.outbound(xUnbluServiceName, toLibHttpRequest(body == null ? new byte[0] : body.getBytes(), context.request().headers())))
                .subscribe()
                .with(
                        response -> writeResponse(context, response),
                        context::fail
                ));
        request.resume();
    }

    private static void writeResponse(RoutingContext context, HttpResponse<String> response) {
        if (response == null) {
            // an empty handler Mono yields a null item through monoToUni; answer 200 like the Spring tier
            context.response().setStatusCode(200).end();
            return;
        }
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
