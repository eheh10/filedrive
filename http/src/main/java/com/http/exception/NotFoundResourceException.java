package com.http.exception;

public class NotFoundResourceException extends RuntimeException{
    public NotFoundResourceException() {
    }

    public NotFoundResourceException(String message) {
        super(message);
    }
}
