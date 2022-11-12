package com.exception;

public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException() {
        super();
    }

    public AlreadyRegisteredException(String msg) {
        super(msg);
    }
}
