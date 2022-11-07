package com.exception;

public class NoSearchResultException extends RuntimeException{
    public NoSearchResultException() {
        super();
    }

    public NoSearchResultException(String msg) {
        super(msg);
    }
}
