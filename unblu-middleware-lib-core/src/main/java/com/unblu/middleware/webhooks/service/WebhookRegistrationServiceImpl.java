
package com.unblu.middleware.webhooks.service;

import com.google.common.base.Strings;
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
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Named("WebhookRegistrationService")
@Singleton
@Slf4j
public class WebhookRegistrationServiceImpl extends RegistrationService<WebhookRegistration> implements WebhookRegistrationService {

    private final WebhookRegistrationsApi webhookRegistrationsApi;
    private final WebhookConfiguration webhookConfiguration;
    private final MiddlewareConfiguration middlewareConfiguration;
    private final Set<EventName> registeredEventNames = new CopyOnWriteArraySet<>();

    public WebhookRegistrationServiceImpl(WebhookRegistrationsApi webhookRegistrationsApi, WebhookConfiguration webhookConfiguration, MiddlewareConfiguration middlewareConfiguration) {
        super(new RegistrationConfiguration(
                middlewareConfiguration.name() + " webhook",
                webhookConfiguration.cleanPrevious(),
                webhookConfiguration.secret()
        ));
        this.webhookRegistrationsApi = webhookRegistrationsApi;
        this.webhookConfiguration = webhookConfiguration;
        this.middlewareConfiguration = middlewareConfiguration;
        Optional.ofNullable(webhookConfiguration.eventNames())
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
        return !middlewareConfiguration.autoRegister() || hasAutoRegistered();
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
        if (!ERegistrationStatus.INACTIVE.equals(webhookRegistration.getStatus())) { // if not manually disabled
            webhookRegistration.setStatus(ERegistrationStatus.ACTIVE);
        }
        return Optional.of(webhookRegistration
                .name(getRegistrationName())
                .description(Strings.isNullOrEmpty(middlewareConfiguration.description()) ? "Registered from Middleware: " + middlewareConfiguration.name() : middlewareConfiguration.description())
                .events(registeredEventNames.stream().map(EventName::name).toList())
                .apiVersion(EWebApiVersion.V4)
                .endpoint(getWebhookUrl())
                .secret(webhookConfiguration.secret()));
    }

    private String getWebhookUrl() {
        return middlewareConfiguration.url() + normalizedPath(webhookConfiguration.apiPath());
    }

    private static String normalizedPath(String configuredPath) {
        var trimmed = configuredPath == null ? "" : configuredPath.trim();
        if (trimmed.isEmpty()) {
            return "/webhook";
        }
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
