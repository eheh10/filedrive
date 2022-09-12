package com.api;

import com.generator.InputStreamTextGenerator;
import com.request.HttpHeaders;

import java.io.IOException;

public interface HttpRequestHandler {
    String handle(HttpHeaders httpHeaders, InputStreamTextGenerator generator) throws IOException;
}
