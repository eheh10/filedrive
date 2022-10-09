package com.releaser;

import com.exception.InputNullParameterException;

import java.io.File;

public class FileResourceCloser implements ResourceCloser {
    private final File value;

    public FileResourceCloser(File value) {
        if (value == null) {
            throw new InputNullParameterException();
        }
        this.value = value;
    }

    @Override
    public boolean close() {
        if (!value.exists()) {
            return false;
        }

        return value.delete();
    }
}
