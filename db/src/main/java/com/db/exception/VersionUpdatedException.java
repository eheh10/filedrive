package com.db.exception;

public class VersionUpdatedException extends RuntimeException{
    public VersionUpdatedException() {
        super();
    }

    public VersionUpdatedException(String msg) {
        super(msg);
    }
}
