package com.exception;

public class NotFoundHttpPathException extends RuntimeException {
    public NotFoundHttpPathException() {
    }

    public NotFoundHttpPathException(String message) {
        super(message);
    }
}
