package com.unblu.middleware.externalmessenger.config;

public record ExternalMessengerConfiguration(
        Long timeoutInMilliSeconds,
        Boolean cleanPrevious,
        Boolean messageStateHandledExternally
) {
    public static ExternalMessengerConfiguration withDefaults() {
        return new ExternalMessengerConfiguration(null, null, null);
    }

    public ExternalMessengerConfiguration {
        timeoutInMilliSeconds = timeoutInMilliSeconds == null ? 1000L : timeoutInMilliSeconds;
        cleanPrevious = cleanPrevious == null ? Boolean.FALSE : cleanPrevious;
        messageStateHandledExternally = messageStateHandledExternally == null ? Boolean.FALSE : messageStateHandledExternally;
    }
}
