package com.http.exception;

public class RequiredLoginException extends RuntimeException{
    public RequiredLoginException() {
    }

    public RequiredLoginException(String message) {
        super(message);
    }
}
