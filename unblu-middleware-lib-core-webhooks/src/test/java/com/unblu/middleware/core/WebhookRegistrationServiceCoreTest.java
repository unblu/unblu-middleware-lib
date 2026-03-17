package com.unblu.middleware.core;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.webhooks.config.WebhookConfiguration;
import com.unblu.middleware.webhooks.entity.EventName;
import com.unblu.middleware.webhooks.service.WebhookRegistrationServiceImpl;
import com.unblu.webapi.jersey.v4.api.WebhookRegistrationsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.WebhookRegistration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.unblu.middleware.common.utils.ObjectUtils.copyOf;
import static com.unblu.middleware.webhooks.entity.EventName.eventNames;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebhookRegistrationServiceCoreTest {

    @Test
    void givenNoPriorRegistration_assertRegisteredCallsCreate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, null);
        var registrationCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));

        service.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(registrationCaptor.capture());
        var registration = registrationCaptor.getValue();
        assertEquals("middleware webhook", registration.getName());
        assertEquals(ERegistrationStatus.ACTIVE, registration.getStatus());
        assertEquals(List.of("something.happened"), registration.getEvents());
    }

    @Test
    void givenNoPriorRegistrationWithMultipleEvents_assertRegisteredCallsCreate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, null);
        var registrationCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));

        service.assertRegistered(eventNames("something.happened", "and.another.something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(registrationCaptor.capture());
        var registration = registrationCaptor.getValue();
        assertEquals("middleware webhook", registration.getName());
        assertEquals(ERegistrationStatus.ACTIVE, registration.getStatus());
        assertEquals(2, registration.getEvents().size());
        assertTrue(registration.getEvents().contains("something.happened"));
        assertTrue(registration.getEvents().contains("and.another.something.happened"));
    }

    @Test
    void givenPriorRegistration_assertRegisteredCallsUpdate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, null);
        var updateCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenReturn(
                new WebhookRegistration().name("middleware webhook")
                        .id("middleware-id")
                        .events(List.of("whatever.nonsense"))
                        .status(ERegistrationStatus.INACTIVE_UNAVAILABLE)
        );

        service.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsUpdate(updateCaptor.capture());
        var registration = updateCaptor.getValue();
        assertEquals("middleware-id", registration.getId());
        assertEquals("middleware webhook", registration.getName());
        assertEquals(ERegistrationStatus.ACTIVE, registration.getStatus());
        assertEquals(List.of("something.happened"), registration.getEvents());
    }

    @Test
    void givenPriorRegistrationAndCleanPrevious_assertRegisteredCallsDeleteThenCreate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, true, null);
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenReturn(
                new WebhookRegistration().name("middleware webhook")
                        .id("middleware-id")
                        .events(List.of("whatever.nonsense"))
                        .status(ERegistrationStatus.INACTIVE)
        );

        service.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsDelete("middleware-id");
        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(createCaptor.capture());
        var registration = createCaptor.getValue();
        assertEquals("middleware webhook", registration.getName());
        assertEquals(ERegistrationStatus.ACTIVE, registration.getStatus());
        assertEquals(List.of("something.happened"), registration.getEvents());
    }

    @Test
    void givenConfiguredEventNames_autoRegisterUsesConfiguration() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, eventNames("something.happened", "another.event"));
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));

        service.autoRegister();

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(createCaptor.capture());
        var registration = createCaptor.getValue();
        assertEquals("middleware webhook", registration.getName());
        assertEquals(ERegistrationStatus.ACTIVE, registration.getStatus());
        assertEquals(2, registration.getEvents().size());
        assertTrue(registration.getEvents().contains("something.happened"));
        assertTrue(registration.getEvents().contains("another.event"));
    }

    @Test
    void givenCustomWebhookApiPath_registrationEndpointUsesConfiguredPath() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, eventNames("something.happened"), "/custom-webhook");
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));

        service.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(createCaptor.capture());
        assertEquals("https://dummy-webhook/custom-webhook", createCaptor.getValue().getEndpoint());
    }

    @Test
    void givenRegistrationAutoDisabled_selfHealRestoresState() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, eventNames("something.happened", "another.event"));
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));
        service.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(createCaptor.capture());
        var created = createCaptor.getValue();
        clearInvocations(webhookRegistrationsApi);

        doReturn(copyOf(created).status(ERegistrationStatus.INACTIVE_UNAVAILABLE))
                .when(webhookRegistrationsApi)
                .webhookRegistrationsGetByName("middleware webhook");

        service.selfHeal();

        verify(webhookRegistrationsApi, atLeastOnce()).webhookRegistrationsUpdate(any(WebhookRegistration.class));
    }

    @Test
    void givenOnlyEventOrderChanged_selfHealDoesNotUpdate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, eventNames("something.happened", "another.event"));
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));
        service.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(createCaptor.capture());
        var created = createCaptor.getValue();
        clearInvocations(webhookRegistrationsApi);

        var reversedEvents = new ArrayList<>(created.getEvents());
        Collections.reverse(reversedEvents);
        doReturn(copyOf(created).events(reversedEvents))
                .when(webhookRegistrationsApi)
                .webhookRegistrationsGetByName("middleware webhook");

        service.selfHeal();

        verify(webhookRegistrationsApi, never()).webhookRegistrationsUpdate(any(WebhookRegistration.class));
    }

    @Test
    void givenRegistrationManuallyDisabled_selfHealDoesNotUpdate() throws ApiException {
        var webhookRegistrationsApi = mock(WebhookRegistrationsApi.class);
        var service = createService(webhookRegistrationsApi, false, eventNames("something.happened", "another.event"));
        var createCaptor = ArgumentCaptor.forClass(WebhookRegistration.class);

        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook"))
                .thenThrow(new ApiException(404, "Not Found"));
        service.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(createCaptor.capture());
        var created = createCaptor.getValue();
        clearInvocations(webhookRegistrationsApi);

        doReturn(copyOf(created).status(ERegistrationStatus.INACTIVE))
                .when(webhookRegistrationsApi)
                .webhookRegistrationsGetByName("middleware webhook");

        service.selfHeal();

        verify(webhookRegistrationsApi, never()).webhookRegistrationsUpdate(any(WebhookRegistration.class));
    }

    private static WebhookRegistrationServiceImpl createService(
            WebhookRegistrationsApi webhookRegistrationsApi,
            boolean cleanPrevious,
            Set<EventName> configuredEventNames
    ) {
        return createService(webhookRegistrationsApi, cleanPrevious, configuredEventNames, "/webhook");
    }

    private static WebhookRegistrationServiceImpl createService(
            WebhookRegistrationsApi webhookRegistrationsApi,
            boolean cleanPrevious,
            Set<EventName> configuredEventNames,
            String apiPath
    ) {
        var middlewareConfiguration = new MiddlewareConfiguration(
                "middleware",
                "",
                "https://dummy-webhook",
                false,
                false,
                true,
                60L,
                true
        );

        var webhookConfiguration = new WebhookConfiguration(
                apiPath,
                "test-secret",
                cleanPrevious,
                configuredEventNames
        );

        return new WebhookRegistrationServiceImpl(
                webhookRegistrationsApi,
                webhookConfiguration,
                middlewareConfiguration
        );
    }
}
