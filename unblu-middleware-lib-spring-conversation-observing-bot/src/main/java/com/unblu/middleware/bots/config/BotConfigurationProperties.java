package com.unblu.middleware.bots.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "unblu")
public class BotConfigurationProperties {

    private BotConfiguration bot;

    public BotConfiguration getBot() {
        return bot;
    }

    public void setBot(BotConfiguration bot) {
        this.bot = bot;
    }

    @Bean
    public BotConfiguration botConfiguration() {
        return bot;
    }
}
