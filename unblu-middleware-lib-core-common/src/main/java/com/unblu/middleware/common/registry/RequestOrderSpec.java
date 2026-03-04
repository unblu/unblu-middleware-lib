package com.unblu.middleware.common.registry;

import java.util.function.Function;

// Spec to determine, which requests should be processed in parallel and which should be processed in order.
// Requests with the same key will be processed in order, requests with different keys can be processed in parallel.
// The key is extracted from the request using the provided keyExtractor function.
public record RequestOrderSpec<T>(
        Function<T, Object> keyExtractor
) {
    // Forces sequential processing of all requests in order in which they are received.
    public static <T> RequestOrderSpec<T> mustPreserveOrder() {
        return new RequestOrderSpec<>(_request -> 0); // all requests will have the same key, preserving the order they are processed
    }

    // Allows parallel processing of all requests
    public static <T> RequestOrderSpec<T> canIgnoreOrder() {
        return new RequestOrderSpec<>(request -> request); // all requests will have different keys, so their processing is parallelized
    }

    // Ensures processing of requests with the same key in order, while allowing parallel processing of requests with different keys.
    public static <T> RequestOrderSpec<T> mustPreserveOrderForThoseWithTheSame(final Function<T, Object> keyExtractor) {
        return new RequestOrderSpec<>(keyExtractor);
    }
}
