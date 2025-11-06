package com.unblu.middleware.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import lombok.experimental.UtilityClass;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

@UtilityClass
public class ObjectUtils {

    private static final ObjectMapper objectMapper = new JSON().getContext(Object.class).copy();

    @SuppressWarnings("unchecked")
    public static <T> T copyOf(T object) {
        try {
            return (T) objectMapper.readValue(objectMapper.writeValueAsString(object), object.getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Copy object error: Can't serialize/deserialize", e);
        }
    }

    // Compares two objects, ignoring order in collections
    public static <T> boolean areTheSame(T object1, T object2) throws JsonProcessingException {
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        String json1 = objectMapper.writeValueAsString(object1);
        String json2 = objectMapper.writeValueAsString(object2);
        var compareResult = JSONCompare.compareJSON(json1, json2, JSONCompareMode.NON_EXTENSIBLE);
        return compareResult.passed();
    }
}
