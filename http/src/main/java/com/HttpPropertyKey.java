package com;

import com.exception.InputNullParameterException;

public enum HttpPropertyKey {
    HTTP_REQUEST_HEADERS_LENGTH_LIMIT("http_request_headers_length_limit"),
    HTTP_REQUEST_BODY_LENGTH_LIMIT("http_request_body_length_limit"),
    NOT_ALLOWED_FILE_EXTENSION("not_allowed_file_extension"),
    HTTP_REQUEST_PROCESS_THREAD_POOL_COUNT("http_request_process_thread_pool_count");

    private final String value;
    HttpPropertyKey(String value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public String value() {
        return value;
    }
}
