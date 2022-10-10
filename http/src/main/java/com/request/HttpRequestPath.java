package com.request;

import com.exception.InputNullParameterException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestPath {
    private final Path value;

    public HttpRequestPath(Path value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value.normalize();
    }

    public static HttpRequestPath of(String path) {
        if (path == null) {
            throw new InputNullParameterException();
        }

        Path value = Paths.get(path);
        return new HttpRequestPath(value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequestPath that = (HttpRequestPath) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}