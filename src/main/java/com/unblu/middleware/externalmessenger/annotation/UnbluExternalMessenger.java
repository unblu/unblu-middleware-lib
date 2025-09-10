package com.unblu.middleware.externalmessenger.annotation;

import com.unblu.middleware.common.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:middleware-application.yml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.externalmessenger"})
public class UnbluExternalMessenger {
}
