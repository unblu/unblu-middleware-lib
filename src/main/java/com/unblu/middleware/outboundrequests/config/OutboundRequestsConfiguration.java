package com.unblu.middleware.outboundrequests.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "unblu.outbound-requests")
@RequiredArgsConstructor
public class OutboundRequestsConfiguration {
    @NotBlank
    private String secret;
    @NotBlank
    private String apiPath = "/outbound";
}
