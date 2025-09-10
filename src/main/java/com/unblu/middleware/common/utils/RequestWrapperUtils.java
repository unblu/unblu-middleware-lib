package com.unblu.middleware.common.utils;

import com.unblu.middleware.common.entity.ContextEntrySpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class RequestWrapperUtils {
    public static <T> List<ContextEntrySpec<Request<T>>> wrapped(Collection<ContextEntrySpec<T>> contextEntries) {
        return contextEntries.stream().map(it -> new ContextEntrySpec<Request<T>>(it.key(), request -> it.valueExtractor().apply(request.body()))).toList();
    }

    public static <T,R> Function<Request<T>, R> wrapped(Function<T, R> action) {
        return request -> action.apply(request.body());
    }

    public static <T> RequestOrderSpec<Request<T>> wrapped(RequestOrderSpec<T> requestOrderSpec) {
        return new RequestOrderSpec<>(request -> requestOrderSpec.keyExtractor().apply(request.body()));
    }
}
