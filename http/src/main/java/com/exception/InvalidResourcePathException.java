package com.exception;

public class InvalidResourcePathException extends RuntimeException{
    public InvalidResourcePathException() {
    }

    public InvalidResourcePathException(String message) {
        super(message);
    }
}
