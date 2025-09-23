package com.unblu.middleware.common.utils;

import com.unblu.middleware.common.entity.ContextSpec;
import com.unblu.middleware.common.entity.Request;
import com.unblu.middleware.common.registry.RequestOrderSpec;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class RequestWrapperUtils {
    public static <T> ContextSpec<Request<T>> wrapped(ContextSpec<T> contextSpec) {
        return ContextSpec.of(contextSpec.contextEntries()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                    e -> (Function<Request<T>, String>) (request -> e.getValue().apply(request.body()))
                )));
    }

    public static <T,R> Function<Request<T>, R> wrapped(Function<T, R> action) {
        return request -> action.apply(request.body());
    }

    public static <T> RequestOrderSpec<Request<T>> wrapped(RequestOrderSpec<T> requestOrderSpec) {
        return new RequestOrderSpec<>(request -> requestOrderSpec.keyExtractor().apply(request.body()));
    }
}
