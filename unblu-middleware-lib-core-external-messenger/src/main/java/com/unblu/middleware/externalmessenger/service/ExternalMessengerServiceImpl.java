package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.outboundrequests.entity.OutboundRequestHandlerOptions;
import com.unblu.middleware.outboundrequests.handler.OutboundHandler;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageRequest;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageResponse;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;
import static com.unblu.middleware.outboundrequests.entity.OutboundRequestType.outboundRequestType;

@Named("ExternalMessengerService")
@Singleton
@Slf4j
@RequiredArgsConstructor
public class ExternalMessengerServiceImpl implements ExternalMessengerService {

    private final OutboundHandler outboundRequestHandler;

    @Override
    public void onWrappedNewMessage(Function<Request<ExternalMessengerNewMessageRequest>, Mono<Void>> action, ContextSpec<Request<ExternalMessengerNewMessageRequest>> contextSpec) {
        outboundRequestHandler.onWrappedMono(
                outboundRequestType("outbound.external_messenger.new_message"),
                ExternalMessengerNewMessageRequest.class,
                ExternalMessengerNewMessageResponse.class,
                _request -> Mono.just(new ExternalMessengerNewMessageResponse()),
                action,
                OutboundRequestHandlerOptions.<Request<ExternalMessengerNewMessageRequest>>requestOrderSpec(
                                mustPreserveOrderForThoseWithTheSame(it -> it.body().getConversationMessage().getId()))
                        .withContextSpec(contextSpec)
        );
    }

    @Override
    public void assertSubscribed() {
        outboundRequestHandler.assertSubscribed();
    }
}
