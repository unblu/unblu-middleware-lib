package com.unblu.middleware.common.annotation;

import com.unblu.middleware.common.config.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@PropertySource(value = "classpath:middleware-application.yml", factory = YamlPropertySourceFactory.class)
@ComponentScan(
        basePackages = "com.unblu.middleware.common",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {AutoConfiguration.class})},
        nameGenerator = org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UnbluMiddlewareLibCommon {
}
