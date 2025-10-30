package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import io.micrometer.context.ContextRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@Configuration
@RequiredArgsConstructor
public class MiddlewareLibBootstrap {

    @Bean
    public ContextRegistry contextRegistry() {
        return ContextRegistry.getInstance();
    }

    @Bean
    public DataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }

    @Bean
    public JSON jerseyJsonResolver() {
        return new JSON();
    }

    @Bean
    public ObjectMapper objectMapper(JSON jerseyJsonResolver) {
        return jerseyJsonResolver.getContext(Object.class);
    }
}
