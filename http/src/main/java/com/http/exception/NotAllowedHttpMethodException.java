package com.http.exception;

public class NotAllowedHttpMethodException extends RuntimeException {
    public NotAllowedHttpMethodException() {
    }

    public NotAllowedHttpMethodException(String message) {
        super(message);
    }
}
