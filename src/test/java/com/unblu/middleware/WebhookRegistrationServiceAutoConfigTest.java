package com.unblu.middleware;

import com.unblu.middleware.webhooks.service.WebhookRegistrationServiceImpl;
import com.unblu.webapi.jersey.v4.api.WebhookRegistrationsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.WebhookRegistration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "unblu.webhook.cleanPrevious=false",
        "unblu.webhook.eventNames=something.happened,another.event",
})
@Slf4j
class WebhookRegistrationServiceAutoConfigTest {

    @Autowired
    WebhookRegistrationServiceImpl webhookRegistrationService;

    @MockitoBean
    WebhookRegistrationsApi webhookRegistrationsApi;

    @Captor
    ArgumentCaptor<WebhookRegistration> registrationCaptor;

    @Test
    void assertThat_autoConfigWorks() throws ApiException {
        when(webhookRegistrationsApi.webhookRegistrationsGetByName("middleware webhook")).thenThrow(new ApiException(404, "Not Found"));

        webhookRegistrationService.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(registrationCaptor.capture());
        var registrations = registrationCaptor.getAllValues();

        assertThat(registrations)
                .extracting(WebhookRegistration::getName)
                .containsExactly("middleware webhook");

        assertThat(registrations)
                .extracting(WebhookRegistration::getStatus)
                .containsExactly(ERegistrationStatus.ACTIVE);

        assertThat(registrations)
                .flatExtracting(WebhookRegistration::getEvents)
                .containsExactly("something.happened", "another.event");
    }
}
