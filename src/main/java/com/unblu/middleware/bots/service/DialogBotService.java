package com.unblu.middleware.bots.service;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.webapi.model.v4.*;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.unblu.middleware.common.utils.RequestWrapperUtils.wrapped;

public interface DialogBotService {

    default void acceptOnboardingOfferIf(Function<BotOnboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOnboardingOfferIf(wrapped(condition));
    }
    default void acceptReboardingOfferIf(Function<BotReboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedReboardingOfferIf(wrapped(condition));
    }
    default void acceptOffboardingOfferIf(Function<BotOffboardingOfferRequest, Mono<Boolean>> condition) {
        acceptWrappedOffboardingOfferIf(wrapped(condition));
    }

    void acceptWrappedOnboardingOfferIf(Function<Request<BotOnboardingOfferRequest>, Mono<Boolean>> condition);
    void acceptWrappedReboardingOfferIf(Function<Request<BotReboardingOfferRequest>, Mono<Boolean>> condition);
    void acceptWrappedOffboardingOfferIf(Function<Request<BotOffboardingOfferRequest>, Mono<Boolean>> condition);

    default void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action) {
        onDialogOpen(action, List.of());
    }
    default void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action) {
        onDialogMessage(action, List.of());
    }
    default void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action) {
        onDialogMessageState(action, List.of());
    }
    default void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action) {
        onDialogCounterpartChanged(action, List.of());
    }
    default void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action) {
        onDialogClosed(action, List.of());
    }

    default void onDialogOpen(Function<BotDialogOpenRequest, Mono<Void>> action, Collection<ContextEntrySpec<BotDialogOpenRequest>> contextEntries) {
        onWrappedDialogOpen(wrapped(action), wrapped(contextEntries));
    }
    default void onDialogMessage(Function<BotDialogMessageRequest, Mono<Void>> action, Collection<ContextEntrySpec<BotDialogMessageRequest>> contextEntries) {
        onWrappedDialogMessage(wrapped(action), wrapped(contextEntries));
    }
    default void onDialogMessageState(Function<BotDialogMessageStateRequest, Mono<Void>> action, Collection<ContextEntrySpec<BotDialogMessageStateRequest>> contextEntries) {
        onWrappedDialogMessageState(wrapped(action), wrapped(contextEntries));
    }
    default void onDialogCounterpartChanged(Function<BotDialogCounterpartChangedRequest, Mono<Void>> action, Collection<ContextEntrySpec<BotDialogCounterpartChangedRequest>> contextEntries) {
        onWrappedDialogCounterpartChanged(wrapped(action), wrapped(contextEntries));
    }
    default void onDialogClosed(Function<BotDialogClosedRequest, Mono<Void>> action, Collection<ContextEntrySpec<BotDialogClosedRequest>> contextEntries) {
        onWrappedDialogClosed(wrapped(action), wrapped(contextEntries));
    }

    void onWrappedDialogOpen(Function<Request<BotDialogOpenRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogOpenRequest>>> contextEntries);
    void onWrappedDialogMessage(Function<Request<BotDialogMessageRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageRequest>>> contextEntries);
    void onWrappedDialogMessageState(Function<Request<BotDialogMessageStateRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogMessageStateRequest>>> contextEntries);
    void onWrappedDialogCounterpartChanged(Function<Request<BotDialogCounterpartChangedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogCounterpartChangedRequest>>> contextEntries);
    void onWrappedDialogClosed(Function<Request<BotDialogClosedRequest>, Mono<Void>> action, Collection<ContextEntrySpec<Request<BotDialogClosedRequest>>> contextEntries);

    void assertSubscribed();
}
