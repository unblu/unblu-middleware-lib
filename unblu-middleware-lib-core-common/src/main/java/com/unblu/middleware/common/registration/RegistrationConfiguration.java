package com.unblu.middleware.common.registration;

public record RegistrationConfiguration(
        String registrationName,
        boolean shouldCleanPrevious,
        String secretKey
) {
}
