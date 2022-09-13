package com.api;

import com.exception.NotAllowedHttpMethodException;
import com.exception.NotFoundHttpPathException;
import com.method.HttpRequestMethod;
import com.path.HttpRequestPath;

import java.util.Map;

public class HttpRequestHandlers {
    private static final Map<HttpRequestPath, Map<HttpRequestMethod, HttpRequestHandler>> VALUES = Map.of(
            HttpRequestPath.of("/test"), Map.of(
                    HttpRequestMethod.POST,new HttpRequestBodyFileCreator()
            )
    );

    public HttpRequestHandler find(HttpRequestPath path, HttpRequestMethod method) {
        if (!VALUES.containsKey(path)) {
            throw new NotFoundHttpPathException();
        }

        Map<HttpRequestMethod,HttpRequestHandler> methodHandler = VALUES.get(path);

        if (!methodHandler.containsKey(method)) {
            throw new NotAllowedHttpMethodException();
        }

        return VALUES.get(path).get(method);
    }
}
