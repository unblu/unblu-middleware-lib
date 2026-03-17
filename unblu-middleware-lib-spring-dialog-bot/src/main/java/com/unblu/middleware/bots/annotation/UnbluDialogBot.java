package com.unblu.middleware.bots.annotation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@AutoConfiguration
@ComponentScan(
        basePackages = "com.unblu.middleware.bots",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {AutoConfiguration.class})},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UnbluDialogBot {
}
