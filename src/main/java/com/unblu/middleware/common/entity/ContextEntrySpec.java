package com.unblu.middleware.common.entity;

import java.util.function.Function;

public record ContextEntrySpec<T>(
    String key,
    Function<T, String> valueExtractor
) {
    // convenience method to create a ContextEntrySpec
    public static <T> ContextEntrySpec<T> contextOf(String key, Function<T, String> valueExtractor) {
        return new ContextEntrySpec<>(key, valueExtractor);
    }
}
