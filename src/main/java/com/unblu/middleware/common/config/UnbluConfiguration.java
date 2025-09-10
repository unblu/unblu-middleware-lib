package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "unblu")
@RequiredArgsConstructor
public class UnbluConfiguration {
    @NotBlank
    private String host;
    @NotBlank
    private String apiBasePath;
    @NotBlank
    private String user;
    @NotBlank
    private String password;
    private String idPropagationHeaderName;
    private String idPropagationUserId;
}
