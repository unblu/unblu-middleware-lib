package com.unblu.middleware.common.automation;

public interface Subscribable {
    void subscribe();
    boolean isSubscribed();
    default void assertSubscribed() {
        if (!isSubscribed()) {
            subscribe();
        }
    }
}
