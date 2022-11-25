package com.request.handler;

import com.HttpMessageStreams;
import com.HttpRequestLengthLimiters;
import com.RetryHttpRequestStream;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;

public interface HttpRequestHandler {
    HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters);
}
