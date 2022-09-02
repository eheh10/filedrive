package com.exception;

public class ExceedingLengthLimitException extends RuntimeException{
    public ExceedingLengthLimitException() {
    }

    public ExceedingLengthLimitException(String message) {
        super(message);
    }
}
