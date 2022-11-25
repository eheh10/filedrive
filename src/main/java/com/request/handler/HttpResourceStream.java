package com.request.handler;

import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;

public class HttpResourceStream implements HttpRequestHandler {
    private final ResourceFinder resourceFinder = new ResourceFinder();
    private final HttpRequestPath defaultPath = HttpRequestPath.of("/images");

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        HttpRequestPath requestFilePath = defaultPath.combine(httpRequestPath);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: ").append(requestFilePath.contentType()).append("\n\n");

        HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());
        HttpMessageStream fileStream = resourceFinder.findGetRequestResource(requestFilePath);

        return responseMsg.sequenceOf(fileStream);
    }
}
