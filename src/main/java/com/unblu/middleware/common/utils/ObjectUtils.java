package com.unblu.middleware.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {

    private static final ObjectMapper objectMapper = new JSON().getContext(Object.class).copy();

    @SuppressWarnings("unchecked")
    public static <T> T copyOf(T registration) {
        try {
            return (T) objectMapper.readValue(objectMapper.writeValueAsString(registration), registration.getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Copy object error: Can't serialize/deserialize", e);
        }
    }
}
