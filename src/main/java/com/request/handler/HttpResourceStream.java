package com.request.handler;

import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;

public class HttpResourceStream implements HttpRequestHandler {
    private static final ResourceFinder RESOURCE_FINDER = new ResourceFinder();
    private static final HttpRequestPath DEFAULT_PATH = HttpRequestPath.of("/images");

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        HttpRequestPath requestFilePath = DEFAULT_PATH.combine(httpRequestPath);

        HttpMessageStream responseHeaders = HttpMessageStream.of("Content-Type: "+requestFilePath.contentType()+";charset=UTF-8");
        HttpMessageStream fileStream = RESOURCE_FINDER.findGetRequestResource(requestFilePath);

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_200,
                responseHeaders,
                fileStream
        );
    }
}
