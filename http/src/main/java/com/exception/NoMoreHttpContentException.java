package com.exception;

public class NoMoreHttpContentException extends RuntimeException {
    public NoMoreHttpContentException() {
    }

    public NoMoreHttpContentException(String message) {
        super(message);
    }
}
