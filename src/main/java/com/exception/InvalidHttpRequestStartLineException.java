package com.exception;

public class InvalidHttpRequestStartLineException extends RuntimeException{
    public InvalidHttpRequestStartLineException() {
    }

    public InvalidHttpRequestStartLineException(String message) {
        super(message);
    }
}
