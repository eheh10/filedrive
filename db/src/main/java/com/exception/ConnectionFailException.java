package com.exception;

public class ConnectionFailException extends RuntimeException{
    public ConnectionFailException() {
        super();
    }

    public ConnectionFailException(String msg) {
        super(msg);
    }
}
