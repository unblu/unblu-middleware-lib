package com.unblu.middleware.externalmessenger.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "unblu.external-messenger")
@RequiredArgsConstructor
public class ExternalMessengerConfiguration {
    private String secret;
    private long timeoutInMilliSeconds = 1000;
    private boolean cleanPrevious = false;
    private boolean messageStateHandledExternally = false;
}
