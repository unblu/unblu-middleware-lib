package com.unblu.middleware.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import lombok.experimental.UtilityClass;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

@UtilityClass
public class ObjectUtils {

    private static final ObjectMapper objectMapper = new JSON().getContext(Object.class).copy();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @SuppressWarnings("unchecked")
    public static <T> T copyOf(T object) {
        try {
            return (T) objectMapper.readValue(objectMapper.writeValueAsString(object), object.getClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Copy object error: Can't serialize/deserialize", e);
        }
    }

    // Compares two objects, ignoring order in collections
    public static <T> JSONCompareResult compare(T object1, T object2) throws JsonProcessingException {
        if (object1 == null && object2 == null) {
            return new JSONCompareResult();
        }
        if (object1 == null || object2 == null) {
            return new JSONCompareResult().fail("One of the objects is null while the other is not", object1, object2);
        }
        String json1 = objectMapper.writeValueAsString(object1);
        String json2 = objectMapper.writeValueAsString(object2);
        return JSONCompare.compareJSON(json1, json2, JSONCompareMode.NON_EXTENSIBLE);
    }

    public static <T> boolean areTheSame(JSONCompareResult compareResult) {
        return compareResult.passed();
    }
}
