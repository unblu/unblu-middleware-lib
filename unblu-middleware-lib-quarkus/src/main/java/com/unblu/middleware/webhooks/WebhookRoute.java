package com.unblu.middleware.webhooks;

import com.unblu.middleware.Utils;
import com.unblu.middleware.webhooks.controller.WebhookControllerService;
import io.quarkus.vertx.web.Body;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestResponse;

import static com.unblu.middleware.Utils.monoToUni;
import static com.unblu.middleware.Utils.toLibHttpRequest;

/**
 * Quarkus reactive route for handling Unblu webhooks.
 */
@ApplicationScoped
@Slf4j
@Path("${unblu.middleware.webhook.api-path:/webhook}")
@RequiredArgsConstructor
public class WebhookRoute {

    private final WebhookControllerService webhookControllerService;

    @POST
    public Uni<RestResponse<Object>> webhook(
            @RestHeader("x-unblu-event") String xUnbluEvent,
            @Body Buffer body,
            @Context HttpHeaders headers
    ) {
        return monoToUni(
                webhookControllerService.webhook(xUnbluEvent, toLibHttpRequest(body, headers))
                        .map(Utils::libHttpResponseToResponseEntity)
        );
    }
}

