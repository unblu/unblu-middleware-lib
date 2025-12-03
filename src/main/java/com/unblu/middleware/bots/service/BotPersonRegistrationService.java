package com.unblu.middleware.bots.service;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.common.error.RegistrationException;
import com.unblu.webapi.jersey.v4.api.PersonsApi;
import com.unblu.webapi.jersey.v4.invoker.ApiException;
import com.unblu.webapi.model.v4.EPersonSource;
import com.unblu.webapi.model.v4.PersonData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotPersonRegistrationService {

    private final PersonsApi personsApi;
    private final BotConfiguration botConfiguration;

    public PersonData assertBotPersonRegistered() throws ApiException {
        try {
            return personsApi.personsGetBySource(EPersonSource.USER_DB, botConfiguration.getPerson().getSourceId(), List.of());
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return createBotPerson();
            } else {
                throw e;
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
