package com.unblu.middleware.common.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
