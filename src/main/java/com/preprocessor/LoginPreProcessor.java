package com.preprocessor;

import com.PreProcessor;
import com.table.SessionStorage;
import com.exception.InputNullParameterException;
import com.exception.InvalidSessionException;
import com.exception.RequiredLoginException;
import com.header.HttpHeaderField;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LoginPreProcessor implements PreProcessor {
    private final SessionStorage sessionStorage = new SessionStorage();
    private final Set<HttpRequestPath> exceptionHttpRequestPaths;

    private LoginPreProcessor(Set<HttpRequestPath> exceptionHttpRequestPaths) {
        if (exceptionHttpRequestPaths == null) {
            throw new InputNullParameterException();
        }

        this.exceptionHttpRequestPaths = Collections.unmodifiableSet(exceptionHttpRequestPaths);
    }

    public static LoginPreProcessor allPathLoginPreProcessor() {
        return new LoginPreProcessor(Collections.emptySet());
    }

    public LoginPreProcessor filtered(HttpRequestPath httpRequestPath) {
        if (httpRequestPath == null) {
            throw new InputNullParameterException();
        }

        Set<HttpRequestPath> exceptionHttpRequestPaths = new HashSet<>(this.exceptionHttpRequestPaths);
        exceptionHttpRequestPaths.add(httpRequestPath);

        return new LoginPreProcessor(exceptionHttpRequestPaths);
    }

    @Override
    public void process(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders) {
        if (exceptionHttpRequestPaths.contains(httpRequestPath)) {
            return;
        }

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        if (sessionStorage.isUnregisteredSession(sessionId)) {
            throw new RequiredLoginException();
        }
    }

    private String searchSessionId(HttpHeaderField cookie) {
        for(String value:cookie.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx),SessionStorage.SESSION_FIELD_NAME)) {
                return value.substring(delIdx+1);
            }
        }
        throw new InvalidSessionException();
    }
}
