package com.response;

public enum HttpResponseStatus {
    CODE_200("OK"),
    CODE_400("Bad Request"),
    CODE_404("Not Found"),
    CODE_405("Method Not Allowed"),
    CODE_413("Request Entity Too Large"),
    CODE_431("Request Header Too Large"),
    CODE_500("Server Error");

    private final String message;
    HttpResponseStatus(String message) {

        this.message = message;
    }

    public String code() {
        return this.name().substring(5);
    }

    public String message() {
        return message;
    }
}
