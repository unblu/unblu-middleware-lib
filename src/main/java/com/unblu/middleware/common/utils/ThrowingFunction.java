package com.unblu.middleware.common.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R> {

    @Override
    default R apply(final T elem) {
        try {
            return applyThrows(elem);
        } catch (final Exception e) {
            if (e instanceof RuntimeException rx) {
                throw rx;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    R applyThrows(T elem) throws Exception;
}