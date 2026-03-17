package com.unblu.middleware.outboundrequests.entity;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import jakarta.validation.constraints.NotNull;

public record OutboundRequestHandlerOptions<T>(
        @NotNull RequestOrderSpec<T> requestOrderSpec,
        @NotNull ContextSpec<T> contextSpec
) {

    public OutboundRequestHandlerOptions() {
        this(RequestOrderSpec.canIgnoreOrder(), ContextSpec.empty());
    }

    public static <T> OutboundRequestHandlerOptions<T> defaults() {
        return new OutboundRequestHandlerOptions<>();
    }

    public OutboundRequestHandlerOptions<T> withRequestOrderSpec(@NotNull RequestOrderSpec<T> requestOrderSpec) {
        return this.requestOrderSpec == requestOrderSpec ? this : new OutboundRequestHandlerOptions<>(requestOrderSpec, this.contextSpec);
    }

    public OutboundRequestHandlerOptions<T> withContextSpec(@NotNull ContextSpec<T> contextSpec) {
        return this.contextSpec == contextSpec ? this : new OutboundRequestHandlerOptions<>(this.requestOrderSpec, contextSpec);
    }

    public static <T> OutboundRequestHandlerOptions<T> requestOrderSpec(@NotNull RequestOrderSpec<T> requestOrderSpec) {
        return new OutboundRequestHandlerOptions<T>().withRequestOrderSpec(requestOrderSpec);
    }

    public static <T> OutboundRequestHandlerOptions<T> contextSpec(@NotNull ContextSpec<T> contextSpec) {
        return new OutboundRequestHandlerOptions<T>().withContextSpec(contextSpec);
    }
}
