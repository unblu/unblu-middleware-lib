package com.unblu.middleware.bots.service;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.webapi.jersey.v4.api.PersonsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.EPersonSource;
import com.unblu.webapi.model.v4.PersonData;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Named
@Singleton
public class BotPersonRegistrationService {

    private final PersonsApi personsApi;
    private final BotConfiguration botConfiguration;

    public PersonData assertBotPersonRegistered() {
        try {
            return personsApi.personsGetBySource(EPersonSource.VIRTUAL, botConfiguration.getPerson().getSourceId(), List.of());
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return createBotPerson();
            } else {
                throw new RegistrationException("Error retrieving bot person: " + e.getMessage(), e);
            }
        }
    }

    private PersonData createBotPerson() {
        var person = botConfiguration.getPerson();
        try {
            return personsApi.personsCreateOrUpdateBot(
                    new PersonData()
                            .firstName(person.getFirstName())
                            .lastName(person.getLastName())
                            .sourceId(person.getSourceId()),
                    List.of()
            );
        } catch (ApiException e) {
            throw new RegistrationException("Error creating bot person: " + e.getMessage(), e);
        }
    }
}
