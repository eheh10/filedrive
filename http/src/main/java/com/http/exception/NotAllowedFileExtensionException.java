package com.http.exception;

public class NotAllowedFileExtensionException extends RuntimeException{
    public NotAllowedFileExtensionException() {
    }

    public NotAllowedFileExtensionException(String message) {
        super(message);
    }
}
