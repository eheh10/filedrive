package com.api;

import com.generator.HttpStringGenerator;
import com.request.HttpHeaders;

import java.io.IOException;

public interface HttpRequestHandler {
    HttpStringGenerator handle(HttpHeaders httpHeaders, HttpStringGenerator generator) throws IOException;
}
