package com.unblu.middleware.externalmessenger.service;

import com.unblu.middleware.common.config.MiddlewareConfiguration;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.middleware.common.registration.RegistrationConfiguration;
import com.unblu.middleware.common.registration.RegistrationService;
import com.unblu.middleware.externalmessenger.config.ExternalMessengerConfiguration;
import com.unblu.webapi.jersey.v4.api.ExternalMessengersApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.CustomExternalMessengerChannel;
import com.unblu.webapi.model.v4.EContactIdentifierFieldType;
import com.unblu.webapi.model.v4.ERegistrationStatus;
import com.unblu.webapi.model.v4.EWebApiVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExternalMessengerRegistrationService extends RegistrationService<CustomExternalMessengerChannel> {

    private final ExternalMessengersApi externalMessengersApi;
    private final ExternalMessengerConfiguration externalMessengerConfiguration;
    private final MiddlewareConfiguration middlewareConfiguration;

    public ExternalMessengerRegistrationService(ExternalMessengersApi externalMessengersApi, ExternalMessengerConfiguration externalMessengerConfiguration, MiddlewareConfiguration middlewareConfiguration) {
        super(new RegistrationConfiguration(
                middlewareConfiguration.getName() + " external messenger",
                externalMessengerConfiguration.isCleanPrevious(),
                externalMessengerConfiguration.getSecret()
        ));
        this.externalMessengersApi = externalMessengersApi;
        this.externalMessengerConfiguration = externalMessengerConfiguration;
        this.middlewareConfiguration = middlewareConfiguration;
    }

    @Override
    protected void callCreateNewRegistration(CustomExternalMessengerChannel channel) throws ApiException {
        externalMessengersApi.externalMessengersCreate(channel, List.of());
    }

    @Override
    protected void callUpdateRegistration(CustomExternalMessengerChannel channel) throws ApiException {
        externalMessengersApi.externalMessengersUpdate(channel, List.of());
    }

    @Override
    protected void callDeleteRegistration(CustomExternalMessengerChannel channel) throws ApiException {
        externalMessengersApi.externalMessengersDelete(channel.getId());
    }

    @Override
    protected CustomExternalMessengerChannel callGetRegistration(String name) throws ApiException {
        var channel = externalMessengersApi.externalMessengersGetByName(name, List.of());
        if (channel instanceof CustomExternalMessengerChannel customExternalMessengerChannel) {
            return customExternalMessengerChannel;
        } else {
            throw new RegistrationException("External messenger with registration " + name + " is not a custom external messenger");
        }
    }

    @Override
    protected CustomExternalMessengerChannel emptyConfiguration() {
        return new CustomExternalMessengerChannel();
    }

    @Override
    protected Optional<CustomExternalMessengerChannel> applyConfiguration(CustomExternalMessengerChannel channel) {
        return Optional.of(channel
                        .name(getRegistrationName())
                        .description(Strings.isBlank(middlewareConfiguration.getDescription()) ? "Registered from Middleware: " + middlewareConfiguration.getName() : middlewareConfiguration.getDescription())
                        .webhookApiVersion(EWebApiVersion.V4)
                        .outboundSupported(true)
                        .webhookStatus(ERegistrationStatus.ACTIVE)
                        .webhookEndpoint(getExternalMessengerUrl())
                        .webhookSecret(externalMessengerConfiguration.getSecret())

                        // TODO following should be configurable
                        .outboundTimeoutMillis(10000L)
                        .contactIdentifierFieldName("id")
                        .contactIdentifierFieldType(EContactIdentifierFieldType.OTHER)
//                .contactIdentifierTranslations()
//                .messageStateHandledExternally()
//                .channelIcon()
//                .supportsMultipleConversationsPerContact()
        );
    }

    private String getExternalMessengerUrl() {
        return middlewareConfiguration.getUrl() + "/external-messenger";
    }
}
