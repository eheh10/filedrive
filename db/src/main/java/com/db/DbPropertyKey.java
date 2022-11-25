package com.db;

import com.db.exception.InputNullParameterException;

public enum DbPropertyKey {
    STORAGE_CAPACITY("storage_capacity");

    private final String value;
    DbPropertyKey(String value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public String value() {
        return value;
    }
}
