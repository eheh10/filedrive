package com.exception;

public class InsertNullException extends RuntimeException {
    public InsertNullException() {
    }

    public InsertNullException(String message) {
        super(message);
    }
}
