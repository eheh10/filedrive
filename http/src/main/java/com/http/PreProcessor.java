package com.http;

import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;

public interface PreProcessor {
    void process(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders);
}
