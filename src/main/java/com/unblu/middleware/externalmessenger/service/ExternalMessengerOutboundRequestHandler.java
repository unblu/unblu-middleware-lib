package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.entity.Request;

public interface ExternalMessengerOutboundRequestHandler {
    <T> void handle(Request<T> request);
}
