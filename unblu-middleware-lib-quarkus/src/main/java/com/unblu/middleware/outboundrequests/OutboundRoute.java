package com.unblu.middleware.outboundrequests;

import com.unblu.middleware.Utils;
import com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService;
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
 * Quarkus reactive route for handling Unblu outbound requests.
 */
@ApplicationScoped
@Slf4j
@Path("${unblu.middleware.outbound-requests.api-path:/outbound}")
@RequiredArgsConstructor
public class OutboundRoute {

    private final OutboundRequestsControllerService outboundRequestsControllerService;

    @POST
    public Uni<RestResponse<Object>> outbound(
            @RestHeader("x-unblu-service-name") String xUnbluServiceName,
            @Body Buffer body,
            @Context HttpHeaders headers
    ) {
        return monoToUni(
                outboundRequestsControllerService.outbound(xUnbluServiceName, toLibHttpRequest(body, headers))
                        .map(Utils::libHttpResponseToResponseEntity)
        );
    }
}

