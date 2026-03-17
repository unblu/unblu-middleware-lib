package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.ExternalMessengerNewMessageRequest;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

// Currently, only on new message is supported. If not called, outbound.external_messenger.new_message cannot not be handled
// And the library will return a failure
public interface ExternalMessengerService {

    default void onNewMessage(Function<ExternalMessengerNewMessageRequest, Mono<Void>> action) {
        onNewMessage(action, ContextSpec.empty());
    }

    default void onNewMessage(Function<ExternalMessengerNewMessageRequest, Mono<Void>> action, ContextSpec<ExternalMessengerNewMessageRequest> contextSpec) {
        onWrappedNewMessage(wrapped(action), wrapped(contextSpec));
    }

    void onWrappedNewMessage(Function<Request<ExternalMessengerNewMessageRequest>, Mono<Void>> action, ContextSpec<Request<ExternalMessengerNewMessageRequest>> contextSpec);

    void assertSubscribed();
}
