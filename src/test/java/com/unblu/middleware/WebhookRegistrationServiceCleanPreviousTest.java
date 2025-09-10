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
        "unblu.webhook.cleanPrevious=true"
})
@Slf4j
class WebhookRegistrationServiceCleanPreviousTest {

    @Autowired
    WebhookRegistrationService webhookRegistrationService;

    @MockitoBean
    WebhookRegistrationsApi webhookRegistrationsApi;

    @Test
    void givenPriorRegistration_whenCleanPrevious_registerWebhooks_callsWebhookRegistrationsDeleteAndCreate() throws ApiException {
        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenReturn(
                new WebhookRegistration().name("middleware webhook")
                        .id("middleware-id")
                        .events(List.of("whatever.nonsense"))
                        .status(ERegistrationStatus.INACTIVE)
        );

        webhookRegistrationService.assertRegistered(eventNames("something.happened"));

        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsDelete("middleware-id");
        verify(webhookRegistrationsApi, times(1)).webhookRegistrationsCreate(assertArg(registration -> {
            assertThat(registration.getName()).isEqualTo("middleware webhook");
            assertThat(registration.getStatus()).isEqualTo(ERegistrationStatus.ACTIVE);
            assertThat(registration.getEvents()).singleElement()
                    .isEqualTo("something.happened");
        }));
    }
}
