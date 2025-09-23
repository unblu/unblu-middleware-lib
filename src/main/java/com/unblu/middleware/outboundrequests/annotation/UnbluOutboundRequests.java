package com.unblu.middleware.outboundrequests.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.outboundrequests"})
@WithUnbluOutboundRequests
public class UnbluOutboundRequests {
}
