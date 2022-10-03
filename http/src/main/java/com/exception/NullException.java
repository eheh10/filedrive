package com.exception;

public class NullException extends RuntimeException{
    public NullException() {
        super();
    }

    public NullException(String msg) {
        super(msg);
    }
}
