package com.http.request.handler;

import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.response.HttpResponseStream;

public interface HttpRequestHandler {
    HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters);
}
