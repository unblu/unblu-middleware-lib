package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
