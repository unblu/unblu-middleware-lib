package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.webapi.model.v4.WebhookRegistration;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.Set;

import static com.unblu.middleware.webhooks.entity.EventName.eventNames;

public interface WebhookRegistrationService {

    default void assertRegistered(String... eventNames) throws RegistrationException {
        assertRegistered(eventNames(eventNames));
    }

    void assertRegistered(@NonNull Set<EventName> eventNames) throws RegistrationException;

    Optional<WebhookRegistration> getRegistration() throws RegistrationException;

    void deleteWebhookRegistration() throws RegistrationException;

    void assertRegistered(@NonNull EventName eventName);

    boolean isRegisteredFor(@NonNull EventName eventName);
}
