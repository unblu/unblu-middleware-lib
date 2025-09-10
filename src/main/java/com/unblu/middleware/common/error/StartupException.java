package com.unblu.middleware.common.error;

public class StartupException extends RuntimeException {
    public StartupException(String message) {
        super(message);
    }

    public StartupException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartupException(Throwable cause) {
        super(cause);
    }
}
