package com.request;

import com.exception.EmptyHttpRequestPathException;
import com.exception.InputNullParameterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestPath {
    private static final HttpRequestPath RESOURCE_REQUEST_PATH = HttpRequestPath.of("!@#$%$%^@$#%");

    private final Path value;

    private HttpRequestPath(Path value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value.normalize();
    }

    public static HttpRequestPath of(String path) {
        if (path == null) {
            throw new InputNullParameterException();
        }

        if (path.isBlank()) {
            throw new EmptyHttpRequestPathException();
        }

        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        Path value = Paths.get(path);
        return new HttpRequestPath(value);
    }

    public static HttpRequestPath ofResourcePath() {
        return RESOURCE_REQUEST_PATH;
    }


    public HttpRequestPath combine(HttpRequestPath httpRequestPath) {
        return new HttpRequestPath(value.resolve(httpRequestPath.value));
    }

    public String contentType() throws IOException {
        return Files.probeContentType(value);
    }

    public boolean isResourcePath() {
        return Objects.equals(this.value,RESOURCE_REQUEST_PATH.value);
    }

    public String getName() {
        return value.getFileName().toString();
    }

    public String getPath() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequestPath that = (HttpRequestPath) o;

        if (this==RESOURCE_REQUEST_PATH || that==RESOURCE_REQUEST_PATH) {
            return true;
        }

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
