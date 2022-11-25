package com.http.exception;

public class EmptyHttpRequestPathException extends RuntimeException{
    public EmptyHttpRequestPathException() {
    }

    public EmptyHttpRequestPathException(String message) {
        super(message);
    }
}
