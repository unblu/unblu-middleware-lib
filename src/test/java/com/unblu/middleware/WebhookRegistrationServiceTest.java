package com.unblu.middleware;

import com.unblu.middleware.webhooks.service.WebhookRegistrationService;
import com.unblu.webapi.jersey.v4.api.WebhookRegistrationsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.WebhookRegistration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.unblu.middleware.webhooks.entity.EventName.eventNames;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "unblu.webhook.cleanPrevious=false",

})
@Slf4j
class WebhookRegistrationServiceTest {

    @Autowired
    WebhookRegistrationService webhookRegistrationService;

    @MockitoBean
    WebhookRegistrationsApi webhookRegistrationsApi;

    @Test
    void givenNoPriorRegistration_registerWebhooks_callsWebhookRegistrationsCreate() throws ApiException {
        webhookRegistrationService.deleteWebhookRegistration();
        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenThrow(new ApiException(404, "Not Found"));

        webhookRegistrationService.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(assertArg(registration -> {
            assertThat(registration.getName()).isEqualTo("middleware webhook");
            assertThat(registration.getStatus()).isEqualTo(ERegistrationStatus.ACTIVE);
            assertThat(registration.getEvents()).singleElement()
                    .isEqualTo("something.happened");
        }));
    }

    @Test
    void givenNoPriorRegistration_registerWebhooksWithMultipleEvents_callsWebhookRegistrationsCreate() throws ApiException {
        webhookRegistrationService.deleteWebhookRegistration();
        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenThrow(new ApiException(404, "Not Found"));

        webhookRegistrationService.assertRegistered(eventNames("something.happened", "and.another.something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(assertArg(registration -> {
            assertThat(registration.getName()).isEqualTo("middleware webhook");
            assertThat(registration.getStatus()).isEqualTo(ERegistrationStatus.ACTIVE);
            assertThat(registration.getEvents()).containsExactlyInAnyOrder("something.happened", "and.another.something.happened");
        }));
    }

    @Test
    void givenPriorRegistration_registerWebhooks_callsWebhookRegistrationsUpdate() throws ApiException {
        webhookRegistrationService.deleteWebhookRegistration();
        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenReturn(
                new WebhookRegistration().name("middleware webhook")
                        .id("middleware-id")
                        .events(List.of("whatever.nonsense"))
                        .status(ERegistrationStatus.INACTIVE)
        );

        webhookRegistrationService.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsUpdate(assertArg(registration -> {
            assertThat(registration.getId()).isEqualTo("middleware-id");
            assertThat(registration.getName()).isEqualTo("middleware webhook");
            assertThat(registration.getStatus()).isEqualTo(ERegistrationStatus.ACTIVE);
            assertThat(registration.getEvents()).singleElement()
                    .isEqualTo("something.happened");
        }));
    }
}
