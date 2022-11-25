package com.http.request.handler;

import com.http.exception.InputNullParameterException;
import com.http.exception.NotAllowedHttpMethodException;
import com.http.exception.NotFoundHttpPathException;
import com.http.request.HttpRequestMethod;
import com.http.request.HttpRequestPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequestHandlers {
    private final Map<HttpRequestPath, Map<HttpRequestMethod, HttpRequestHandler>> values = new HashMap<>();

    public void register(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        if (path == null || method == null || handler == null) {
            throw new InputNullParameterException();
        }

        values.put(path,Map.of(method,handler));
    }

    public HttpRequestHandler find(HttpRequestPath path, HttpRequestMethod method) {
        if (path == null || method == null) {
            throw new InputNullParameterException();
        }

        path = convertPathIfResourceRequest(path,method);

        if (!values.containsKey(path) ) {
            throw new NotFoundHttpPathException();
        }

        Map<HttpRequestMethod,HttpRequestHandler> methodHandler = values.get(path);

        if (!methodHandler.containsKey(method)) {
            throw new NotAllowedHttpMethodException();
        }

        return values.get(path).get(method);
    }
    private HttpRequestPath convertPathIfResourceRequest(HttpRequestPath path, HttpRequestMethod method) {
        if (values.containsKey(path) && path.isResourcePath()) {
            throw new NotFoundHttpPathException();
        }
        if (Objects.equals(method, HttpRequestMethod.GET) && !values.containsKey(path)) {
            return HttpRequestPath.ofResourcePath();
        }
        return path;
    }
}
