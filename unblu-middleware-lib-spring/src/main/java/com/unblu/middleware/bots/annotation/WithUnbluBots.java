package com.unblu.middleware.bots.annotation;

import com.unblu.middleware.common.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

@Configuration
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PropertySource(value = "classpath:middleware-application.yml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.outboundrequests", "com.unblu.middleware.bots"})
public @interface WithUnbluBots {
}
