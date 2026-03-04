package com.unblu.middleware.bots.config;

import com.unblu.webapi.model.v4.EBotDialogFilter;
import com.unblu.webapi.model.v4.EBotDialogTimeoutBehavior;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BotConfiguration(
        @Min(500) @Max(30000) Long timeoutInMilliSeconds,
        Boolean cleanPrevious,
        @Valid BotPerson person,
        EBotDialogFilter onboardingFilter,
        @Min(0) Integer onboardingOrder,
        EBotDialogFilter offboardingFilter,
        @Min(0) Integer offboardingOrder,
        Boolean reboardingEnabled,
        @Min(0) Integer reboardingOrder,
        EBotDialogTimeoutBehavior onTimeoutBehavior,
        Boolean messageStateHandledExternally,
        Boolean automaticTypingStateHandlingEnabled,
        Boolean needsCounterpartPresence,
        @Min(0) @Max(5) Long retryCount,
        @Min(0) @Max(10000) Long retryDelayInMilliSeconds
) {
    public BotConfiguration {
        timeoutInMilliSeconds = timeoutInMilliSeconds == null ? 10000L : timeoutInMilliSeconds;
        cleanPrevious = cleanPrevious == null ? Boolean.FALSE : cleanPrevious;
        person = person == null ? new BotPerson("Unblu", "Concierge", "concierge-bot-person-id") : person;
        onboardingFilter = onboardingFilter == null ? EBotDialogFilter.NONE : onboardingFilter;
        onboardingOrder = onboardingOrder == null ? 100 : onboardingOrder;
        offboardingFilter = offboardingFilter == null ? EBotDialogFilter.NONE : offboardingFilter;
        offboardingOrder = offboardingOrder == null ? 100 : offboardingOrder;
        reboardingEnabled = reboardingEnabled == null ? Boolean.FALSE : reboardingEnabled;
        reboardingOrder = reboardingOrder == null ? 100 : reboardingOrder;
        onTimeoutBehavior = onTimeoutBehavior == null ? EBotDialogTimeoutBehavior.ABORT : onTimeoutBehavior;
        messageStateHandledExternally = messageStateHandledExternally == null ? Boolean.FALSE : messageStateHandledExternally;
        automaticTypingStateHandlingEnabled = automaticTypingStateHandlingEnabled == null ? Boolean.TRUE : automaticTypingStateHandlingEnabled;
        needsCounterpartPresence = needsCounterpartPresence == null ? Boolean.TRUE : needsCounterpartPresence;
        retryCount = retryCount == null ? 3L : retryCount;
        retryDelayInMilliSeconds = retryDelayInMilliSeconds == null ? 1000L : retryDelayInMilliSeconds;
    }

    public record BotPerson(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String sourceId
    ) {
        public BotPerson {
            firstName = firstName == null || firstName.isBlank() ? "Unblu" : firstName;
            lastName = lastName == null || lastName.isBlank() ? "Concierge" : lastName;
            sourceId = sourceId == null || sourceId.isBlank() ? "concierge-bot-person-id" : sourceId;
        }
    }
}
