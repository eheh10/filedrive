package com.exception;

public class NotFoundPage extends RuntimeException {
    public NotFoundPage() {
    }

    public NotFoundPage(String message) {
        super(message);
    }
}
