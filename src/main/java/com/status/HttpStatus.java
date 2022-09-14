package com.status;

public enum HttpStatus {
    code200("OK"),
    code400("Bad Request"),
    code404("Not Found"),
    code405("Method Not Allowed"),
    code413("Request Entity Too Large"),
    code431("Request header too large"),
    code500("Server Error");

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
