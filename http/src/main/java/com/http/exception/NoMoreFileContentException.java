package com.http.exception;

public class NoMoreFileContentException extends RuntimeException{
    public NoMoreFileContentException() {
    }

    public NoMoreFileContentException(String message) {
        super(message);
    }
}
