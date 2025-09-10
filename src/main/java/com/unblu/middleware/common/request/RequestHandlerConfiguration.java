package com.unblu.middleware.common.request;

import lombok.NonNull;

public record RequestHandlerConfiguration(
        @NonNull String secretKey
) {
}
