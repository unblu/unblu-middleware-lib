package com.unblu.middleware.externalmessenger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "unblu")
public class ExternalMessengerConfigurationProperties {

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
        return externalMessenger;
    }
}
