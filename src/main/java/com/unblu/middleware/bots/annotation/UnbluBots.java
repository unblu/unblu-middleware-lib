package com.unblu.middleware.bots.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.outboundrequests", "com.unblu.middleware.bots"})
@WithUnbluBots
public class UnbluBots {
}
