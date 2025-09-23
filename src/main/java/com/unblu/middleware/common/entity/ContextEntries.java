package com.unblu.middleware.common.entity;

import java.util.Collection;
import java.util.List;

public record ContextEntries<T>(
        Collection<ContextEntrySpec<T>> contextEntries
) {
    public static <T> ContextEntries<T> of(Collection<ContextEntrySpec<T>> contextEntries) {
        return new ContextEntries<>(contextEntries);
    }

    public static <T> ContextEntries<T> empty() {
        return new ContextEntries<>(List.of());
    }
}
