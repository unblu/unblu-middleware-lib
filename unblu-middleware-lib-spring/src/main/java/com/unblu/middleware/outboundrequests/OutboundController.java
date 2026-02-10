package com.unblu.middleware.outboundrequests;

import com.unblu.middleware.Utils;
import com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "${unblu.outbound-requests.api-path}", method = RequestMethod.POST)
public class OutboundController {

    private final OutboundRequestsControllerService outboundRequestsControllerService;

    public OutboundController(OutboundRequestsControllerService outboundRequestsControllerService) {
        this.outboundRequestsControllerService = outboundRequestsControllerService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> outbound(
            @RequestHeader("x-unblu-service-name") String xUnbluServiceName,
            @RequestBody Flux<DataBuffer> bodyBuffer,
            @RequestHeader HttpHeaders headers
    ) {

        return Utils.toLibHttpRequest(bodyBuffer, headers)
                .flatMap(rawRequest -> outboundRequestsControllerService.outbound(xUnbluServiceName, rawRequest))
                .map(Utils::libHttpResponseToResponseEntity);
    }
}
