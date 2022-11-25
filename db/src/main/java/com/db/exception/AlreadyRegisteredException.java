package com.db.exception;

public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException() {
        super();
    }

    public AlreadyRegisteredException(String msg) {
        super(msg);
    }
}
