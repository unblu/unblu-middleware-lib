package com.unblu.middleware.outboundrequests;

import com.unblu.middleware.Utils;
import com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService;
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
 * Quarkus reactive route for handling Unblu outbound requests.
 */
@ApplicationScoped
@Slf4j
@Path("/outbound")
@RequiredArgsConstructor
public class OutboundRoute {

    private final OutboundRequestsControllerService outboundRequestsControllerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> outbound(
            @RestHeader("x-unblu-service-name") String xUnbluServiceName,
            byte[] body,
            @Context HttpHeaders headers
    ) {
        return monoToUni(
                outboundRequestsControllerService.outbound(xUnbluServiceName, toLibHttpRequest(body, headers))
                        .map(Utils::libHttpResponseToResponseEntity)
        );
    }
}
