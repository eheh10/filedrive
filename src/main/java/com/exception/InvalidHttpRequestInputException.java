package com.exception;

public class InvalidHttpRequestInputException extends RuntimeException{
    public InvalidHttpRequestInputException() {
    }

    public InvalidHttpRequestInputException(String message) {
        super(message);
    }
}
