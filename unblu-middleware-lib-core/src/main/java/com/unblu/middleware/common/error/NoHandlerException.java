package com.unblu.middleware.common.error;

public class NoHandlerException extends RuntimeException {

    public NoHandlerException(String message) {
        super(message);
    }

    public NoHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoHandlerException(Throwable cause) {
        super(cause);
    }
}
