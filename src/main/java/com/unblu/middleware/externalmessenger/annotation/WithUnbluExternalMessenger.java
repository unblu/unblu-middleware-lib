package com.unblu.middleware.externalmessenger.annotation;

import com.unblu.middleware.common.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PropertySource(value = "classpath:middleware-application.yml", factory = YamlPropertySourceFactory.class)
@ComponentScan(basePackages = {"com.unblu.middleware.common", "com.unblu.middleware.externalmessenger"})
public @interface WithUnbluExternalMessenger {
}
