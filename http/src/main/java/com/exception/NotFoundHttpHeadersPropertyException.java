package com.exception;

public class NotFoundHttpHeadersPropertyException extends RuntimeException{
    public NotFoundHttpHeadersPropertyException() {
    }

    public NotFoundHttpHeadersPropertyException(String message) {
        super(message);
    }
}
