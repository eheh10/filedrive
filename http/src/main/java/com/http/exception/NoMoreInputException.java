package com.http.exception;

public class NoMoreInputException extends RuntimeException {
    public NoMoreInputException() {
    }

    public NoMoreInputException(String message) {
        super(message);
    }
}
