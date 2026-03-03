package com.unblu.middleware.webhooks;

import com.unblu.middleware.Utils;
import com.unblu.middleware.webhooks.controller.WebhookControllerService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestHeader;

import static com.unblu.middleware.Utils.monoToUni;
import static com.unblu.middleware.Utils.toLibHttpRequest;

/**
 * Quarkus reactive route for handling Unblu webhooks.
 */
@ApplicationScoped
@Slf4j
@Path("/webhook")
@RequiredArgsConstructor
public class WebhookRoute {

    private final WebhookControllerService webhookControllerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> webhook(
            @RestHeader("x-unblu-event") String xUnbluEvent,
            byte[] body,
            @Context HttpHeaders headers
    ) {
        return monoToUni(
                webhookControllerService.webhook(xUnbluEvent, toLibHttpRequest(body, headers))
                        .map(Utils::libHttpResponseToResponseEntity)
        );
    }
}
