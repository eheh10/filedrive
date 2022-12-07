package com.request.handler;

import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.request.HttpRequestPagePath;
import com.http.request.HttpRequestPath;
import com.http.response.HttpResponseStatus;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestPageStream implements HttpRequestHandler {

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: text/html;charset=UTF-8\n\n");

        HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());

        String pagePath = HttpRequestPagePath.of(httpRequestPath.getName()).path();
        InputStream pageIs = null;
        try {
            pageIs = new FileInputStream(pagePath);
        } catch (FileNotFoundException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
        }
        HttpMessageStream pageHtml = HttpMessageStream.of(pageIs);

        return responseMsg.sequenceOf(pageHtml);
    }

    private HttpMessageStreams createRedirectionResponse(HttpResponseStatus status) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(status.code()).append(" ")
                .append(status.message()).append("\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);

        return HttpMessageStreams.of(responseStream);
    }
}
