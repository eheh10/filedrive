package com.exception;

public class MustBePositiveNumberException extends RuntimeException{
    public MustBePositiveNumberException() {
    }

    public MustBePositiveNumberException(String message) {
        super(message);
    }
}
