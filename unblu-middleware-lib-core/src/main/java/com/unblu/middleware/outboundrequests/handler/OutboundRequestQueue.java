package com.unblu.middleware.outboundrequests.handler;

import com.unblu.middleware.common.error.FatalStartupErrorHandler;
import com.unblu.middleware.common.registry.ContextRegistryWrapper;
import com.unblu.middleware.common.registry.RequestQueue;
import com.unblu.middleware.common.registry.RequestQueueErrorHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named
@Singleton
public class OutboundRequestQueue extends RequestQueue {
    public OutboundRequestQueue(FatalStartupErrorHandler fatalStartupErrorHandler, ContextRegistryWrapper contextRegistryWrapper, RequestQueueErrorHandler requestQueueErrorHandler) {
        super(fatalStartupErrorHandler, contextRegistryWrapper, requestQueueErrorHandler);
    }
}
