package com.status;

public enum HttpStatus {
    CODE_200("OK"),
    CODE_400("Bad Request"),
    CODE_404("Not Found"),
    CODE_405("Method Not Allowed"),
    CODE_413("Request Entity Too Large"),
    CODE_431("Request header too large"),
    CODE_500("Server Error");

    private final String message;
    HttpStatus(String message) {

        this.message = message;
    }

    public String code() {
        return this.name().substring(4);
    }

    public String message() {
        return message;
    }
}
