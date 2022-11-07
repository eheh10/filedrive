package com.exception;

public class InputNullParameterException extends RuntimeException{
    public InputNullParameterException() {
        super();
    }

    public InputNullParameterException(String msg) {
        super(msg);
    }
}
