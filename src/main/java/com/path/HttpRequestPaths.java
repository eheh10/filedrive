package com.path;

import com.api.HttpApi;
import com.api.TestHttpApi;

public enum HttpRequestPaths {
    test(new TestHttpApi());

    private final HttpApi api;
    HttpRequestPaths(HttpApi api) {
        this.api = api;
    }

    public HttpApi binding() {
        return api;
    }
}
