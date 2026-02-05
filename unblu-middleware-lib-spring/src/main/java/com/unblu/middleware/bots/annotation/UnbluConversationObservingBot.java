package com.unblu.middleware.bots.annotation;

import com.unblu.middleware.bots.config.BotConfiguration;
import com.unblu.middleware.bots.service.BotPersonRegistrationService;
import com.unblu.middleware.common.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.unblu.middleware.common"})
@Import({BotPersonRegistrationService.class, BotConfiguration.class})
@PropertySource(value = "classpath:middleware-application.yml", factory = YamlPropertySourceFactory.class)
public class UnbluConversationObservingBot {
}
