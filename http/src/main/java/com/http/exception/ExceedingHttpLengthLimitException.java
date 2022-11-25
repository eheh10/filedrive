package com.http.exception;

public class ExceedingHttpLengthLimitException extends RuntimeException{
    public ExceedingHttpLengthLimitException() {
    }

    public ExceedingHttpLengthLimitException(String message) {
        super(message);
    }
}
