package com.unblu.middleware.outboundrequests;

import com.unblu.middleware.Utils;
import com.unblu.middleware.outboundrequests.controller.OutboundRequestsControllerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${unblu.outbound-requests.api-path}", method = RequestMethod.POST)
public class OutboundController {

    private final OutboundRequestsControllerService outboundRequestsControllerService;

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
