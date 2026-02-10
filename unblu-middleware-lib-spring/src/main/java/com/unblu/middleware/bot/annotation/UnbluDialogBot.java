package com.unblu.middleware.bot.annotation;

import com.unblu.middleware.outboundrequests.annotation.UnbluOutboundRequests;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(UnbluOutboundRequests.class)
@ComponentScan(
        basePackages = "com.unblu.middleware.bots",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {AutoConfiguration.class})},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UnbluDialogBot {
}
