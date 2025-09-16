package com.unblu.middleware;

import com.unblu.middleware.bots.service.BotRegistrationService;
import com.unblu.middleware.externalmessenger.service.ExternalMessengerRegistrationService;
import com.unblu.middleware.webhooks.service.WebhookRegistrationServiceImpl;
import com.unblu.webapi.jersey.v4.api.WebhookRegistrationsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.WebhookRegistration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.unblu.middleware.common.utils.ObjectUtils.copyOf;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "unblu.webhook.cleanPrevious=false",
        "unblu.middleware.selfHealingEnabled=true",
        "unblu.middleware.selfHealingCheckIntervalInSeconds=1",
        "unblu.webhook.eventNames=something.happened,another.event",
})
@Slf4j
class WebhookRegistrationServiceSelfHealingTest {

    @Autowired
    WebhookRegistrationServiceImpl webhookRegistrationService;

    @MockitoBean
    WebhookRegistrationsApi webhookRegistrationsApi;

    // gotta mock these two to prevent them from trying to self-heal
    @MockitoBean
    BotRegistrationService botRegistrationService;
    @MockitoBean
    ExternalMessengerRegistrationService externalMessengerRegistrationService;

    @Captor
    ArgumentCaptor<WebhookRegistration> registrationCaptor;

    @Test
    @SneakyThrows
    void givenRegistrationGetsDeactivated_selfHealing_restoreItsState() {
        doThrow(new ApiException(404, "Not Found"))
                .when(webhookRegistrationsApi)
                .webhookRegistrationsGetByName("middleware webhook");

        webhookRegistrationService.autoRegister();

        verify(webhookRegistrationsApi).webhookRegistrationsCreate(registrationCaptor.capture());
        var registration = registrationCaptor.getValue();

        doReturn(copyOf(registration).status(ERegistrationStatus.INACTIVE_UNAVAILABLE))
                .when(webhookRegistrationsApi)
                .webhookRegistrationsGetByName("middleware webhook");

        // wait for self-healing to kick in
        await().atMost(3, SECONDS).untilAsserted(() ->
                verify(webhookRegistrationsApi, atLeast(1)).webhookRegistrationsUpdate(registration));
    }
}
