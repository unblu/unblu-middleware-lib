package com.unblu.middleware.bots.config;

import com.unblu.webapi.model.v4.EBotDialogFilter;
import com.unblu.webapi.model.v4.EBotDialogTimeoutBehavior;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "unblu.bot")
@RequiredArgsConstructor
public class BotConfiguration {
    private long timeoutInMilliSeconds = 1000;
    private boolean cleanPrevious = false;
    @Valid
    private BotPerson person = new BotPerson();
    private EBotDialogFilter onboardingFilter = EBotDialogFilter.NONE;
    private int onboardingOrder = 100;
    private EBotDialogFilter offboardingFilter = EBotDialogFilter.NONE;
    private int offboardingOrder = 100;
    private boolean reboardingEnabled = false;
    private int reboardingOrder = 100;
    private EBotDialogTimeoutBehavior onTimeoutBehavior = EBotDialogTimeoutBehavior.ABORT;
    private boolean messageStateHandledExternally = false;
    private boolean automaticTypingStateHandlingEnabled = true;
    private boolean needsCounterpartPresence = true;

    @Data
    @RequiredArgsConstructor
    public static class BotPerson {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotBlank
        private String sourceId;
    }
}
