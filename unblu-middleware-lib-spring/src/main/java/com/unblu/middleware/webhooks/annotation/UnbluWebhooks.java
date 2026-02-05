package com.unblu.middleware.webhooks.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.webhooks"})
@WithUnbluWebhooks
public class UnbluWebhooks {
}
