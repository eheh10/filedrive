package com.db.exception;

public class InvalidSessionException extends RuntimeException{
    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String msg) {
        super(msg);
    }
}
