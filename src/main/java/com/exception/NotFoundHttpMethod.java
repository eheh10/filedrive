package com.exception;

public class NotFoundHttpMethod extends RuntimeException {
    public NotFoundHttpMethod() {
    }

    public NotFoundHttpMethod(String message) {
        super(message);
    }
}
