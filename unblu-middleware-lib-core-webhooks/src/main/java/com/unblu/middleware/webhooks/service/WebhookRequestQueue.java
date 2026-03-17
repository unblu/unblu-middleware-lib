package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueErrorHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named
@Singleton
public class WebhookRequestQueue extends RequestQueue {
    public WebhookRequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper, RequestQueueErrorHandler requestQueueErrorHandler) {
        super(fatalStartupErrorHandler, contextRegistryWrapper, requestQueueErrorHandler);
    }
}
