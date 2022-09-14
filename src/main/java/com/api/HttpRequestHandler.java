package com.api;

import com.generator.HttpStringGenerator;
import com.request.HttpHeaders;
import com.request.StringLengthLimit;

import java.io.IOException;

public interface HttpRequestHandler {
    HttpStringGenerator handle(HttpHeaders httpHeaders, HttpStringGenerator generator, StringLengthLimit requestBodyLengthLimit) throws IOException;
}
