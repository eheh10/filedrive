package com.property;

import com.http.exception.InputNullParameterException;

public enum PropertyKey {
    FILE_DOWNLOAD_COUNT_LIMIT("file_download_count_limit");
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
