package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.handler.OutboundRequestHandler;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageRequest;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalMessengerServiceImpl implements ExternalMessengerService {

    private final OutboundRequestHandler outboundRequestHandler;

    @Override
    public void onWrappedNewMessage(Function<Request<ExternalMessengerNewMessageRequest>, Mono<Void>> action, ContextSpec<Request<ExternalMessengerNewMessageRequest>> contextSpec) {
        outboundRequestHandler.registerHandler(
                outboundRequestType("outbound.external_messenger.new_message"),
                ExternalMessengerNewMessageRequest.class,
                ExternalMessengerNewMessageResponse.class,
                _request -> Mono.just(new ExternalMessengerNewMessageResponse()),
                action,
                mustPreserveOrderForThoseWithTheSame(it -> it.body().getConversationMessage().getId()),
                contextSpec
        );
    }

    @Override
    public void assertSubscribed() {
        outboundRequestHandler.assertSubscribed();
    }
}
