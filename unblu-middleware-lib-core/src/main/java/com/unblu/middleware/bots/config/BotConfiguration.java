package com.unblu.middleware.bots.config;

import com.unblu.webapi.model.v4.EBotDialogFilter;
import com.unblu.webapi.model.v4.EBotDialogTimeoutBehavior;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BotConfiguration {
    @Min(500)
    @Max(30000)
    private long timeoutInMilliSeconds = 10000;
    private boolean cleanPrevious = false;
    @Valid
    private BotPerson person = new BotPerson();
    private EBotDialogFilter onboardingFilter = EBotDialogFilter.NONE;
    @Min(0)
    private int onboardingOrder = 100;
    private EBotDialogFilter offboardingFilter = EBotDialogFilter.NONE;
    @Min(0)
    private int offboardingOrder = 100;
    private boolean reboardingEnabled = false;
    @Min(0)
    private int reboardingOrder = 100;
    private EBotDialogTimeoutBehavior onTimeoutBehavior = EBotDialogTimeoutBehavior.ABORT;
    private boolean messageStateHandledExternally = false;
    private boolean automaticTypingStateHandlingEnabled = true;
    private boolean needsCounterpartPresence = true;

    @Min(0)
    @Max(5)
    private long retryCount = 3;

    @Min(0)
    @Max(10000)
    private long retryDelayInMilliSeconds = 1000;

    @Data
    @RequiredArgsConstructor
    public static class BotPerson {
        @NotBlank
        private String firstName = "Unblu";
        @NotBlank
        private String lastName = "Concierge";
        @NotBlank
        private String sourceId = "concierge-bot-person-id";
    }
}
