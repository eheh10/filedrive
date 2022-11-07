package com.request.handler;

import com.HttpMessageStreams;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;

import java.io.IOException;

public interface HttpRequestHandler {
    HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStreams bodyStream) throws IOException;
}
