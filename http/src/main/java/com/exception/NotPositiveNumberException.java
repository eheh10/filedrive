package com.exception;

public class NotPositiveNumberException extends RuntimeException{
    public NotPositiveNumberException() {
    }

    public NotPositiveNumberException(String message) {
        super(message);
    }
}
