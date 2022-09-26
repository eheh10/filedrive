package com.exception;

public class NotFoundKeyException extends RuntimeException{
    public NotFoundKeyException() {
    }

    public NotFoundKeyException(String message) {
        super(message);
    }
}
