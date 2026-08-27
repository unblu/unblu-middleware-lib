package com.unblu.middleware.bots.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "unblu")
public class BotConfigurationProperties {

    @Valid
    @NestedConfigurationProperty
    private BotConfiguration bot;

    public BotConfiguration getBot() {
        return bot;
    }

    public void setBot(BotConfiguration bot) {
        this.bot = bot;
    }

    @Bean
    public BotConfiguration botConfiguration() {
        // the section is optional (all fields have defaults) — never expose a null bean
        return bot == null ? BotConfiguration.withDefaults() : bot;
    }
}
