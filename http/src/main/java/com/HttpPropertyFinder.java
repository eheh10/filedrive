package com;

import com.exception.InputNullParameterException;
import com.exception.NotFoundResourceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class HttpPropertyFinder {
    private final Properties value = new Properties();

    public HttpPropertyFinder() {
        try {
            value.load(new FileInputStream(Path.of("http","http.properties").toFile()));
        } catch (IOException e) {
            throw new NotFoundResourceException("Not Found http.properties");
        }
    }

    private String find(HttpPropertyKey key) {
        if (key == null) {
            throw new InputNullParameterException();
        }
        return value.getProperty(key.value());
    }

    public int httpRequestHeadersLengthLimit() {
        return Integer.parseInt(find(HttpPropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT));
    }

    public int httpRequestBodyLengthLimit() {
        return Integer.parseInt(find(HttpPropertyKey.HTTP_REQUEST_BODY_LENGTH_LIMIT));
    }

    public int httpRequestProcessThreadPoolCount() {
        return Integer.parseInt(find(HttpPropertyKey.HTTP_REQUEST_PROCESS_THREAD_POOL_COUNT));
    }

    public Set<String> notAllowedFileExtension() {
        Set<String> extensions = new HashSet<>(List.of(find(HttpPropertyKey.NOT_ALLOWED_FILE_EXTENSION).split(",")));
        return Collections.unmodifiableSet(extensions);
    }
}