package com.unblu.middleware.outboundrequests.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutboundRequestsConfiguration {
    @NotBlank
    private String secret;
    @NotBlank
    private String apiPath = "/outbound";
}
