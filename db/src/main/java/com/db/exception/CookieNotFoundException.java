package com.db.exception;

public class CookieNotFoundException extends RuntimeException{
    public CookieNotFoundException() {
        super();
    }

    public CookieNotFoundException(String msg) {
        super(msg);
    }
}
