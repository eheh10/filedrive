package com.exception;

public class PropertyNotFoundException extends RuntimeException{
    public PropertyNotFoundException() {
        super();
    }

    public PropertyNotFoundException(String msg) {
        super(msg);
    }
}
