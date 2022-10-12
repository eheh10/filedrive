package com;

import com.exception.InputNullParameterException;

public enum PropertyKey {
    HTTP_REQUEST_HEADERS_LENGTH_LIMIT("http_request_headers_length_limit"),
    HTTP_REQUEST_BODY_LENGTH_LIMIT("http_request_body_length_limit"),
    NOT_ALLOWED_EXTENSION("not_allowed_extension");

    private final String value;
    PropertyKey(String value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public String value() {
        return value;
    }
}
