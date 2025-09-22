package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageRequest;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

public interface ExternalMessengerService {

    default void onNewMessage(Function<ExternalMessengerNewMessageRequest, Mono<Void>> action) {
        onNewMessage(action, List.of());
    }

    default void onNewMessage(Function<ExternalMessengerNewMessageRequest, Mono<Void>> action, Collection<ContextEntrySpec<ExternalMessengerNewMessageRequest>> contextEntries) {
        onWrappedNewMessage(wrapped(action), wrapped(contextEntries));
    }

    void onWrappedNewMessage(Function<Request<ExternalMessengerNewMessageRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<ExternalMessengerNewMessageRequest>>> contextEntries);

    void assertSubscribed();
}
