package com.db.exception;

public class NotFoundPropertyException extends RuntimeException{
    public NotFoundPropertyException() {
        super();
    }

    public NotFoundPropertyException(String msg) {
        super(msg);
    }
}
