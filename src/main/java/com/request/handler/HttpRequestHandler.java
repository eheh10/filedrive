package com.request.handler;

import com.HttpStreamGenerator;
import com.header.HttpHeaders;
import com.HttpLengthLimiter;

import java.io.IOException;

public interface HttpRequestHandler {
    HttpStreamGenerator handle(HttpHeaders httpHeaders, HttpStreamGenerator generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException;
}
