package com.unblu.middleware.bots.service;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.middleware.common.registration.RegistrationConfiguration;
import com.unblu.middleware.common.registration.RegistrationService;
import com.unblu.middleware.outboundrequests.config.OutboundRequestsConfiguration;
import com.unblu.webapi.jersey.v4.api.BotsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.CustomDialogBotData;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.EWebApiVersion;
import com.unblu.webapi.model.v4.PersonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BotRegistrationService extends RegistrationService<CustomDialogBotData> {

    private final BotsApi botsApi;
    private final BotConfiguration botConfiguration;
    private final MiddlewareConfiguration middlewareConfiguration;
    private final OutboundRequestsConfiguration outboundRequestsConfiguration;
    private final BotPersonRegistrationService botPersonRegistrationService;

    public BotRegistrationService(BotsApi botsApi, BotConfiguration botConfiguration, MiddlewareConfiguration middlewareConfiguration, OutboundRequestsConfiguration outboundRequestsConfiguration, BotPersonRegistrationService botPersonRegistrationService) {
        super(new RegistrationConfiguration(
                middlewareConfiguration.getName() + " bot",
                botConfiguration.isCleanPrevious(),
                outboundRequestsConfiguration.getSecret()
        ));
        this.botsApi = botsApi;
        this.botConfiguration = botConfiguration;
        this.middlewareConfiguration = middlewareConfiguration;
        this.outboundRequestsConfiguration = outboundRequestsConfiguration;
        this.botPersonRegistrationService = botPersonRegistrationService;
    }

    @Override
    protected void callCreateNewRegistration(CustomDialogBotData registration) throws ApiException {
        botsApi.botsCreate(withBotPerson(registration));
    }

    @Override
    protected void callUpdateRegistration(CustomDialogBotData registration) throws ApiException {
        botsApi.botsUpdate(withBotPerson(registration));
    }

    @Override
    protected void callDeleteRegistration(CustomDialogBotData registration) throws ApiException {
        botsApi.botsDelete(registration.getId());
    }

    @Override
    protected CustomDialogBotData callGetRegistration(String registrationName) throws ApiException {
        var botData = botsApi.botsGetByName(registrationName);
        if (botData instanceof CustomDialogBotData customDialogBotData) {
            return customDialogBotData;
        } else {
            throw new RegistrationException("Bot with registration " + registrationName + " is not custom dialog bot");
        }
    }

    @Override
    protected CustomDialogBotData emptyConfiguration() {
        return new CustomDialogBotData();
    }

    private CustomDialogBotData withBotPerson(CustomDialogBotData botData) throws ApiException {
        var botPerson = botPersonRegistrationService.assertBotPersonRegistered();
        return botData.botPersonId(botPerson.getId());
    }

    /**
     * @deprecated use BotPersonRegistrationService.assertBotPersonRegistered() instead
     */
    @Deprecated
    public PersonData assertBotPersonRegistered() {
        return botPersonRegistrationService.assertBotPersonRegistered();
    }

    @Override
    protected Optional<CustomDialogBotData> applyConfiguration(CustomDialogBotData botData) {
        if (!ERegistrationStatus.INACTIVE.equals(botData.getWebhookStatus())) { // if not manually disabled
            botData.setWebhookStatus(ERegistrationStatus.ACTIVE);
        }
        return Optional.of(botData
                .webhookApiVersion(EWebApiVersion.V4)
                .name(getRegistrationName())
                .description(Strings.isBlank(middlewareConfiguration.getDescription()) ? "Registered from Middleware: " + middlewareConfiguration.getName() : middlewareConfiguration.getDescription())
                .webhookEndpoint(getBotUrl())
                .webhookSecret(outboundRequestsConfiguration.getSecret())
                .outboundTimeoutMillis(botConfiguration.getTimeoutInMilliSeconds())
                .onboardingOrder(botConfiguration.getOnboardingOrder())
                .reboardingOrder(botConfiguration.getReboardingOrder())
                .onboardingFilter(botConfiguration.getOnboardingFilter())
                .offboardingOrder(botConfiguration.getOffboardingOrder())
                .offboardingFilter(botConfiguration.getOffboardingFilter())
                .onTimeoutBehavior(botConfiguration.getOnTimeoutBehavior())
                .messageStateHandledExternally(botConfiguration.isMessageStateHandledExternally())
                .automaticTypingStateHandlingEnabled(botConfiguration.isAutomaticTypingStateHandlingEnabled())
                .needsCounterpartPresence(botConfiguration.isNeedsCounterpartPresence())
                .retryCount(botConfiguration.getRetryCount())
                .retryDelay(botConfiguration.getRetryDelayInMilliSeconds()));
    }

    private String getBotUrl() {
        return middlewareConfiguration.getUrl() + outboundRequestsConfiguration.getApiPath();
    }
}
