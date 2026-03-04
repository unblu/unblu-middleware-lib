package com.unblu.middleware.webhooks.annotation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@AutoConfiguration
@ComponentScan(
        basePackages = "com.unblu.middleware.webhooks",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {AutoConfiguration.class})},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UnbluWebhooks {
}
