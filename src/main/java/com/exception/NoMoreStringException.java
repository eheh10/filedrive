package com.exception;

public class NoMoreStringException extends RuntimeException {
    public NoMoreStringException() {
    }

    public NoMoreStringException(String message) {
        super(message);
    }
}
