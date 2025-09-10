package com.unblu.middleware.common.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {

    @Override
    default void run() {
        try {
            runThrows();
        } catch (final Exception e) {
            if (e instanceof RuntimeException rx) {
                throw rx;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void runThrows() throws Exception;
}