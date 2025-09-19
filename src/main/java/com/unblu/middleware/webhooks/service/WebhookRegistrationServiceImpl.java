
package com.unblu.middleware.webhooks.service;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.middleware.common.registration.RegistrationConfiguration;
import com.unblu.middleware.common.registration.RegistrationService;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.webapi.jersey.v4.api.WebhookRegistrationsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.EWebApiVersion;
import com.unblu.webapi.model.v4.WebhookRegistration;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
public class WebhookRegistrationServiceImpl extends RegistrationService<WebhookRegistration> implements WebhookRegistrationService {

    private final WebhookRegistrationsApi webhookRegistrationsApi;
    private final WebhookConfiguration webhookConfiguration;
    private final MiddlewareConfiguration middlewareConfiguration;
    private final Set<EventName> registeredEventNames = new CopyOnWriteArraySet<>();

    public WebhookRegistrationServiceImpl(WebhookRegistrationsApi webhookRegistrationsApi, WebhookConfiguration webhookConfiguration, MiddlewareConfiguration middlewareConfiguration) {
        super(new RegistrationConfiguration(
                middlewareConfiguration.getName() + " webhook",
                webhookConfiguration.isCleanPrevious(),
                webhookConfiguration.getSecret()
        ));
        this.webhookRegistrationsApi = webhookRegistrationsApi;
        this.webhookConfiguration = webhookConfiguration;
        this.middlewareConfiguration = middlewareConfiguration;
        Optional.ofNullable(webhookConfiguration.getEventNames())
                .ifPresent(registeredEventNames::addAll);
    }

    @Override
    public void assertRegistered(@NonNull EventName eventName) {
        assertRegistered(Set.of(eventName));
    }

    @Override
    public boolean isRegisteredFor(@NonNull EventName eventName) {
        return registeredEventNames.contains(eventName);
    }

    @Override
    public void assertRegistered(@NonNull Set<EventName> eventNames) {
        if (registeredEventNames.containsAll(eventNames)) {
            return;
        }
        registeredEventNames.addAll(eventNames);

        if (shouldReconcile()) {
            reconcile();
        }
    }

    // Don't reconcile during the initialization if auto-registration is enabled - to avoid unnecessary API calls because
    // the initial registration will include all events from the configuration anyway.
    // This is to avoid calling registration updates when registering multiple webhooks using subsequent webhookHandlerService.on...() calls
    private boolean shouldReconcile() {
        return !middlewareConfiguration.isAutoRegister() || hasAutoRegistered();
    }

    @Override
    public void deleteWebhookRegistration() throws RegistrationException {
        registeredEventNames.clear();
        deleteRegistration();
    }

    @Override
    protected void callCreateNewRegistration(WebhookRegistration registration) throws ApiException {
        webhookRegistrationsApi.webhookRegistrationsCreate(registration);
    }

    @Override
    protected void callUpdateRegistration(WebhookRegistration registration) throws ApiException {
        webhookRegistrationsApi.webhookRegistrationsUpdate(registration);
    }

    @Override
    protected void callDeleteRegistration(WebhookRegistration registration) throws ApiException {
        webhookRegistrationsApi.webhookRegistrationsDelete(registration.getId());
    }

    @Override
    protected WebhookRegistration callGetRegistration(String registrationName) throws ApiException {
        return webhookRegistrationsApi.webhookRegistrationsGetByName(registrationName);
    }

    @Override
    protected WebhookRegistration emptyConfiguration() {
        return new WebhookRegistration();
    }

    @Override
    protected Optional<WebhookRegistration> applyConfiguration(WebhookRegistration webhookRegistration) {
        if (registeredEventNames.isEmpty()) {
            return Optional.empty(); // No events to register, skip registration
        }
        return Optional.of(webhookRegistration
                .status(ERegistrationStatus.ACTIVE)
                .name(getRegistrationName())
                .description(Strings.isBlank(middlewareConfiguration.getDescription()) ? "Registered from Middleware: " + middlewareConfiguration.getName() : middlewareConfiguration.getDescription())
                .events(registeredEventNames.stream().map(EventName::name).toList())
                .apiVersion(EWebApiVersion.V4)
                .endpoint(getWebhookUrl())
                .secret(webhookConfiguration.getSecret()));
    }

    private String getWebhookUrl() {
        return middlewareConfiguration.getUrl() + "/webhook";
    }
}
