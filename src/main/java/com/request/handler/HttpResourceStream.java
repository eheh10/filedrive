package com.request.handler;

import com.*;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpResourceStream implements HttpRequestHandler {
    private final ResourceFinder resourceFinder = new ResourceFinder();
    private final HttpRequestPath defaultPath = HttpRequestPath.of("/images");

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStream bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        HttpRequestPath requestFilePath = defaultPath.combine(httpRequestPath);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: ").append(requestFilePath.contentType()).append("\n\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);
        HttpMessageStreams responseMsg = HttpMessageStreams.of(responseStream);

        HttpMessageStream fileStream = resourceFinder.findGetRequestResource(requestFilePath);

        return responseMsg.sequenceOf(fileStream);
    }
}
