package com.unblu.middleware.bots.service;

import com.google.common.base.Strings;
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
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Named
@Singleton
public class BotRegistrationService extends RegistrationService<CustomDialogBotData> {

    private final BotsApi botsApi;
    private final BotConfiguration botConfiguration;
    private final MiddlewareConfiguration middlewareConfiguration;
    private final OutboundRequestsConfiguration outboundRequestsConfiguration;
    private final BotPersonRegistrationService botPersonRegistrationService;

    public BotRegistrationService(BotsApi botsApi, BotConfiguration botConfiguration, MiddlewareConfiguration middlewareConfiguration, OutboundRequestsConfiguration outboundRequestsConfiguration, BotPersonRegistrationService botPersonRegistrationService) {
        super(new RegistrationConfiguration(
                middlewareConfiguration.name() + " bot",
                botConfiguration.cleanPrevious(),
                outboundRequestsConfiguration.secret()
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
                .description(Strings.isNullOrEmpty(middlewareConfiguration.description()) ? "Registered from Middleware: " + middlewareConfiguration.name() : middlewareConfiguration.description())
                .webhookEndpoint(getBotUrl())
                .webhookSecret(outboundRequestsConfiguration.secret())
                .outboundTimeoutMillis(botConfiguration.timeoutInMilliSeconds())
                .onboardingOrder(botConfiguration.onboardingOrder())
                .reboardingOrder(botConfiguration.reboardingOrder())
                .onboardingFilter(botConfiguration.onboardingFilter())
                .offboardingOrder(botConfiguration.offboardingOrder())
                .offboardingFilter(botConfiguration.offboardingFilter())
                .onTimeoutBehavior(botConfiguration.onTimeoutBehavior())
                .messageStateHandledExternally(botConfiguration.messageStateHandledExternally())
                .automaticTypingStateHandlingEnabled(botConfiguration.automaticTypingStateHandlingEnabled())
                .needsCounterpartPresence(botConfiguration.needsCounterpartPresence())
                .retryCount(botConfiguration.retryCount())
                .retryDelay(botConfiguration.retryDelayInMilliSeconds()));
    }

    private String getBotUrl() {
        return middlewareConfiguration.url() + outboundRequestsConfiguration.apiPath();
    }
}
