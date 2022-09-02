package com.exception;

public class StatusCode431Exception extends RuntimeException{
    public StatusCode431Exception() {
        super("431 Request header too large");
    }

    public StatusCode431Exception(String message) {
        super(message);
    }
}
