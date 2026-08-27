package com.unblu.middleware.externalmessenger.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "unblu")
public class ExternalMessengerConfigurationProperties {

    @Valid
    @NestedConfigurationProperty
    private ExternalMessengerConfiguration externalMessenger;

    public ExternalMessengerConfiguration getExternalMessenger() {
        return externalMessenger;
    }

    public void setExternalMessenger(ExternalMessengerConfiguration externalMessenger) {
        this.externalMessenger = externalMessenger;
    }

    @Bean
    public ExternalMessengerConfiguration externalMessengerConfiguration() {
        // the section is optional (all fields have defaults) — never expose a null bean
        return externalMessenger == null ? ExternalMessengerConfiguration.withDefaults() : externalMessenger;
    }
}
