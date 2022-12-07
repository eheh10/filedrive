package com.request.handler;

import com.db.exception.InvalidSessionException;
import com.db.table.SessionStorage;
import com.http.HttpMessageStreams;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;

import java.util.Objects;

public class HttpSessionCloser implements HttpRequestHandler {
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder response = new StringBuilder();

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        SESSION_STORAGE.expireSession(sessionId);

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/login\n");

        return HttpMessageStreams.of(response.toString());
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
