package com.request;

import java.nio.file.Paths;

public enum HttpRequestPagePath {
    MAIN(Paths.get("src","main","resources","main.html").toString()),
    SIGN_UP(Paths.get("src","main","resources","signUp.html").toString()),
    LOGIN(Paths.get("src","main","resources","login.html").toString()),
    UPLOAD(Paths.get("src","main","resources","upload.html").toString());

    private final String resourcePath;
    HttpRequestPagePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public static HttpRequestPagePath of(String targetPage) {
        targetPage = targetPage.toUpperCase();
        return HttpRequestPagePath.valueOf(targetPage);
    }

    public String path() {
        return resourcePath;
    }
}
