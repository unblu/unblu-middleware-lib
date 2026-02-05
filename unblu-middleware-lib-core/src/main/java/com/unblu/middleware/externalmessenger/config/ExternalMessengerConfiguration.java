package com.unblu.middleware.externalmessenger.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ExternalMessengerConfiguration {
    private long timeoutInMilliSeconds = 1000;
    private boolean cleanPrevious = false;
    private boolean messageStateHandledExternally = false;
}
