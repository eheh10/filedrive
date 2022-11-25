package com.http.exception;

public class NoMoreHttpContentException extends RuntimeException {
    public NoMoreHttpContentException() {
    }

    public NoMoreHttpContentException(String message) {
        super(message);
    }
}
