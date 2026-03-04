package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "unblu")
public class MiddlewareLibConfigurationProperties {
    private MiddlewareConfiguration middleware;

    // must unwrap & copy for compatibility :-(( 
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

    @Bean
    public MiddlewareConfiguration middlewareConfiguration() {
        return middleware;
    }

    @Bean
    public UnbluConfiguration unbluConfiguration() {
        return new UnbluConfiguration(host, apiBasePath, user, password, idPropagationHeaderName, idPropagationUserId);
    }
}
