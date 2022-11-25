package com;

import com.header.HttpHeaders;
import com.request.HttpRequestPath;

public interface PreProcessor {
    void process(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders);
}
