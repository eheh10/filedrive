package com.request.handler;

import com.HttpLengthLimiter;
import com.HttpsStream;
import com.header.HttpHeaders;

import java.io.IOException;

public interface HttpRequestHandler {
    HttpsStream handle(HttpHeaders httpHeaders, HttpsStream generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException;
}
