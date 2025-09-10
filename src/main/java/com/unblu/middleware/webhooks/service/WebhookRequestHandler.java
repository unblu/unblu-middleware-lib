package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.webhooks.entity.EventName;
import org.springframework.http.HttpHeaders;

public interface WebhookRequestHandler {
    void handle(EventName eventName, byte[] body, HttpHeaders headers);
}
