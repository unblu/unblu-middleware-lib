package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueServiceImpl;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Function;

import static com.unblu.middleware.common.registry.RequestOrderSpec.mustPreserveOrderForThoseWithTheSame;

@Service
@Slf4j
public class ExternalMessengerServiceImpl extends RequestQueueServiceImpl implements ExternalMessengerService, ExternalMessengerOutboundRequestHandler {

    public ExternalMessengerServiceImpl(RequestQueue requestQueue) {
        super(requestQueue);
    }

    @Override
    public <T> void handle(Request<T> request) {
        requestQueue.queueRequest(request);
    }

    @Override
    public void onWrappedNewMessage(Function<Request<ExternalMessengerNewMessageRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<ExternalMessengerNewMessageRequest>>> contextEntries) {
        requestQueue.onWrapped(ExternalMessengerNewMessageRequest.class, action, mustPreserveOrderForThoseWithTheSame(it -> it.body().getConversationMessage().getId()), contextEntries);
    }

    @Override
    public void subscribe() {
        getFlux().subscribe();
    }

    @Override
    public Flux<Void> getFlux() {
        return requestQueue.getFlux();
    }
}
