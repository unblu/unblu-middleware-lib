package com.unblu.middleware.common.entity;

import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ContextSpec<T>(Map<String, Function<T, String>> contextEntries) {

    public static <T> ContextSpec<T> empty() {
        return ContextSpec.of();
    }

    public static <T> ContextSpec<T> of(Map<String, Function<T, String>> contextEntries) {
        return new ContextSpec<>(Map.copyOf(contextEntries));
    }

    public static <T> ContextSpec<T> of() {
        return ContextSpec.of(Map.of());
    }

    public static <T> ContextSpec<T> of(String s1, Function<T, String> f1) {
        return ContextSpec.of(Map.of(s1, f1));
    }

    public static <T> ContextSpec<T> of(
            String s1, Function<T, String> f1,
            String s2, Function<T, String> f2
    ) {
        return ContextSpec.of(Map.of(s1, f1, s2, f2));
    }

    public static <T> ContextSpec<T> of(
            String s1, Function<T, String> f1,
            String s2, Function<T, String> f2,
            String s3, Function<T, String> f3
    ) {
        return ContextSpec.of(Map.of(s1, f1, s2, f2, s3, f3));
    }

    public static <T> ContextSpec<T> of(
            String s1, Function<T, String> f1,
            String s2, Function<T, String> f2,
            String s3, Function<T, String> f3,
            String s4, Function<T, String> f4
    ) {
        return ContextSpec.of(Map.of(s1, f1, s2, f2, s3, f3, s4, f4));
    }

    public static <T> ContextSpec<T> of(
            String s1, Function<T, String> f1,
            String s2, Function<T, String> f2,
            String s3, Function<T, String> f3,
            String s4, Function<T, String> f4,
            String s5, Function<T, String> f5
    ) {
        return ContextSpec.of(Map.of(s1, f1, s2, f2, s3, f3, s4, f4, s5, f5));
    }

    public ContextSpec<T> putAll(Map<String, Function<T, String>> contextEntries) {
        var combined = new HashMap<>(this.contextEntries);
        combined.putAll(contextEntries);
        return new ContextSpec<>(Map.copyOf(combined));
    }

    public Context applyTo(T t) {
        return Context.of(
                contextEntries.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().apply(t)
                )));
    }
}
