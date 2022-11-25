package com.http.exception;

public class NotFoundTemplateException extends RuntimeException{
    public NotFoundTemplateException() {
    }

    public NotFoundTemplateException(String message) {
        super(message);
    }
}
