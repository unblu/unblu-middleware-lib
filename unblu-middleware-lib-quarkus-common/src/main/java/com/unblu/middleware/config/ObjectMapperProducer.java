package com.unblu.middleware.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.middleware.common.utils.ObjectUtils;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public class ObjectMapperProducer {

    @Produces
    @Singleton
    public ObjectMapper objectMapper(Instance<ObjectMapperCustomizer> customizers) {
        var mapper = ObjectUtils.getObjectMapper().copy();
        for (var customizer : customizers) {
            customizer.customize(mapper);
        }
        return mapper;
    }
}
