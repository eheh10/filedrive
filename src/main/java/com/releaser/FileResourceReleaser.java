package com.releaser;

import com.exception.NullException;

import java.io.File;

public class FileResourceReleaser implements ResourceReleaser{
    private final File value;

    public FileResourceReleaser(File value) {
        if (value == null) {
            throw new NullException();
        }
        this.value = value;
    }

    @Override
    public boolean release() {
        if (!value.exists()) {
            return false;
        }

        return value.delete();
    }
}
