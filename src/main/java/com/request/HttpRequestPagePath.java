package com.request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public enum HttpRequestPagePath {
    MAIN("main"),
    SIGN_UP("signUp"),
    LOGIN("login"),
    UPLOAD("upload"),
    DOWNLOAD("download");

    private static final Path DEFAULT_PATH = Paths.get("src","main","resources");
    private static final Map<String, HttpRequestPagePath> pagePaths = createPagePaths();

    private final String pageName;

    HttpRequestPagePath(String pageName) {
        this.pageName = pageName;
    }

    private static Map<String, HttpRequestPagePath> createPagePaths() {
        Map<String, HttpRequestPagePath> values = new HashMap<>();

        for (HttpRequestPagePath pagePath : HttpRequestPagePath.values()) {
            values.put(pagePath.pageName,pagePath);
        }
        return Collections.unmodifiableMap(values);
    }

    public static HttpRequestPagePath of(String targetPageName) {
        return pagePaths.get(targetPageName);
    }

    public String path() {
        return DEFAULT_PATH.resolve(pageName+".html").toString();
    }
}
