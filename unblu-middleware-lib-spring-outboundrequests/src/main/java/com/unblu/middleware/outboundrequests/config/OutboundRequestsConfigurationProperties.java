package com.unblu.middleware.outboundrequests.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@ConfigurationProperties(prefix = "unblu")
public class OutboundRequestsConfigurationProperties {

    @Valid
    @NestedConfigurationProperty
    private OutboundRequestsConfiguration outboundRequests;

    public OutboundRequestsConfiguration getOutboundRequests() {
        return outboundRequests;
    }

    public void setOutboundRequests(OutboundRequestsConfiguration outboundRequests) {
        this.outboundRequests = outboundRequests;
    }

    @Bean
    public OutboundRequestsConfiguration outboundRequestsConfiguration() {
        return outboundRequests;
    }
}
