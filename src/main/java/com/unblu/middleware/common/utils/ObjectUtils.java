package com.unblu.middleware.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unblu.webapi.jersey.v4.invoker.JSON;
import lombok.experimental.UtilityClass;
import org.skyscreamer.jsonassert.JSONAssert;
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

    public static <T> boolean areLenientEqual(T object1, T object2) {
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        try {
            String json1 = objectMapper.writeValueAsString(object1);
            String json2 = objectMapper.writeValueAsString(object2);
            JSONAssert.assertEquals(json1, json2, JSONCompareMode.LENIENT);
            JSONAssert.assertEquals(json2, json1, JSONCompareMode.LENIENT);
            return true;
        } catch (AssertionError e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Weak equal error: Can't serialize objects", e);
        }
    }
}
