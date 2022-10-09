package com.request.handler;

import com.exception.NotAllowedHttpMethodException;
import com.exception.NotFoundHttpPathException;
import com.exception.InputNullParameterException;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandlers {
    private static Map<HttpRequestPath, Map<HttpRequestMethod, HttpRequestHandler>> values = new HashMap<>();

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

        if (!values.containsKey(path)) {
            throw new NotFoundHttpPathException();
        }

        Map<HttpRequestMethod,HttpRequestHandler> methodHandler = values.get(path);

        if (!methodHandler.containsKey(method)) {
            throw new NotAllowedHttpMethodException();
        }

        return values.get(path).get(method);
    }
}
