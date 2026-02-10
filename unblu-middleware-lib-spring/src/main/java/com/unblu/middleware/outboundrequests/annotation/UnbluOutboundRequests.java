package com.unblu.middleware.outboundrequests.annotation;

import com.unblu.middleware.common.annotation.UnbluMiddlewareLibCommon;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(UnbluMiddlewareLibCommon.class)
@ComponentScan(
        basePackages = "com.unblu.middleware.outboundrequests",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {AutoConfiguration.class})},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class UnbluOutboundRequests {
}
