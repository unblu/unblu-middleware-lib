package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        // a copy: consumers customizing the injected mapper must not mutate the
        // JVM-static instance the library uses internally (Quarkus does the same)
        return ObjectUtils.getObjectMapper().copy();
    }
}
