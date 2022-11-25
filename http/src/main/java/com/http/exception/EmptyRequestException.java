package com.http.exception;

public class EmptyRequestException extends RuntimeException {
    public EmptyRequestException() {
    }

    public EmptyRequestException(String message) {
        super(message);
    }
}
