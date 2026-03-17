package com.unblu.middleware.bots.annotation;

import com.unblu.middleware.bots.config.BotConfigurationProperties;
import com.unblu.middleware.bots.registration.BotPersonRegistrationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnMissingClass("com.unblu.middleware.bots.service.DialogBot")
// otherwise beans are double-defined
@Import({
        BotPersonRegistrationService.class,
        BotConfigurationProperties.class,
})
public class UnbluConversationObservingBot {
}
