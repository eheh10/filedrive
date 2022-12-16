package com.http;

import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundPropertyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class HttpPropertyFinder {
    private static final String PROPERTY_FILE_PATH = Path.of("http","http.properties").toString();
    private static final HttpPropertyFinder INSTANCE = new HttpPropertyFinder(createProperties());
    private final Properties value;

    private HttpPropertyFinder(Properties value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public static HttpPropertyFinder getInstance() {
        return INSTANCE;
    }

    private static Properties createProperties() {
        try {
            Properties value = new Properties();
            value.load(new FileInputStream(PROPERTY_FILE_PATH));

            return value;
        } catch (IOException e) {
            throw new NotFoundPropertyException("Not Found http.properties");
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
