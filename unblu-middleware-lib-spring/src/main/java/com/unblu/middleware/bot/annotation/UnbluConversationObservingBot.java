package com.unblu.middleware.bot.annotation;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.bots.service.BotPersonRegistrationService;
import com.unblu.middleware.common.annotation.UnbluMiddlewareLibCommon;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnMissingBean({
        UnbluDialogBot.class,
})
// otherwise beans are double-defined
@Import({
        UnbluMiddlewareLibCommon.class,
        BotPersonRegistrationService.class,
        BotConfiguration.class,
})
public class UnbluConversationObservingBot {
}
