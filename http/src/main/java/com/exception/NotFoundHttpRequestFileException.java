package com.exception;

public class NotFoundHttpRequestFileException extends RuntimeException{
    public NotFoundHttpRequestFileException() {
    }

    public NotFoundHttpRequestFileException(String message) {
        super(message);
    }
}
