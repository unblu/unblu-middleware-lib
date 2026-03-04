package com.unblu.middleware.webhooks.entity;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import jakarta.validation.constraints.NotNull;

public record WebhookHandlerOptions<T>(@NotNull RequestOrderSpec<T> requestOrderSpec,
                                       @NotNull ContextSpec<T> contextSpec, boolean shouldAssertRegistered) {

    public WebhookHandlerOptions() {
        this(RequestOrderSpec.canIgnoreOrder(), ContextSpec.empty(), true);
    }

    public static <T> WebhookHandlerOptions<T> defaults() {
        return new WebhookHandlerOptions<T>();
    }

    public WebhookHandlerOptions<T> withRequestOrderSpec(@NotNull RequestOrderSpec<T> requestOrderSpec) {
        return this.requestOrderSpec == requestOrderSpec ? this : new WebhookHandlerOptions<T>(requestOrderSpec, this.contextSpec, this.shouldAssertRegistered);
    }

    public WebhookHandlerOptions<T> withContextSpec(@NotNull ContextSpec<T> contextSpec) {
        return this.contextSpec == contextSpec ? this : new WebhookHandlerOptions<T>(this.requestOrderSpec, contextSpec, this.shouldAssertRegistered);
    }

    public WebhookHandlerOptions<T> andDontAssertRegistered() {
        return !this.shouldAssertRegistered ? this : new WebhookHandlerOptions<T>(this.requestOrderSpec, this.contextSpec, false);
    }



    public static <T> WebhookHandlerOptions<T> requestOrderSpec(@NotNull RequestOrderSpec<T> requestOrderSpec) {
        return new WebhookHandlerOptions<T>().withRequestOrderSpec(requestOrderSpec);
    }

    public static <T> WebhookHandlerOptions<T> contextSpec(@NotNull ContextSpec<T> contextSpec) {
        return new WebhookHandlerOptions<T>().withContextSpec(contextSpec);
    }

    public static <T> WebhookHandlerOptions<T> dontAssertRegistered() {
        return new WebhookHandlerOptions<T>().andDontAssertRegistered();
    }
}
