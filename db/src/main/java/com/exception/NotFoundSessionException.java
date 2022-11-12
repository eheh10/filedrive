package com.exception;

public class NotFoundSessionException extends RuntimeException{
    public NotFoundSessionException() {
        super();
    }

    public NotFoundSessionException(String msg) {
        super(msg);
    }
}
