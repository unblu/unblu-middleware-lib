package com.unblu.middleware.webhooks.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record EventName(String name) {
    public static EventName eventName(String name) {
        return new EventName(name);
    }

    public static Set<EventName> eventNames(String... names) {
        return Arrays.stream(names)
                .map(EventName::eventName)
                .collect(Collectors.toSet());
    }
}
