package com.path;

import com.exception.NullException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestPath {
    private final Path value;

    public HttpRequestPath(Path value) {
        if (value == null) {
            throw new NullException();
        }

        this.value = value;
    }

    public static HttpRequestPath of(String path) {
        Path value = Paths.get(path).normalize();
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
        return value != null ? value.hashCode() : 0;
    }
}
