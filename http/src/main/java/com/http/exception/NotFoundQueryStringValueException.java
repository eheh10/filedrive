package com.http.exception;

public class NotFoundQueryStringValueException extends RuntimeException{
    public NotFoundQueryStringValueException() {
    }

    public NotFoundQueryStringValueException(String message) {
        super(message);
    }
}
