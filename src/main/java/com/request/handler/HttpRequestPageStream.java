package com.request.handler;

import com.*;
import com.exception.InputNullParameterException;
import com.exception.NotFoundHttpHeadersPropertyException;
import com.header.HttpHeaderField;
import com.header.HttpHeaders;
import com.request.HttpRequestPagePath;
import com.request.HttpRequestPath;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpRequestPageStream implements HttpRequestHandler {
    private final boolean requiredLogin;
    private final SessionStorage sessionStorage = new SessionStorage();

    public HttpRequestPageStream(boolean requiredLogin) {
        this.requiredLogin = requiredLogin;
    }

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStream bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        if (requiredLogin) {
            HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
            String sessionId = searchSessionId(cookie);

            if(sessionId == null) {
                return createRedirectionResponse(HttpResponseStatus.CODE_304);
            }

            if (!sessionStorage.validSession(sessionId)) {
                return createRedirectionResponse(HttpResponseStatus.CODE_401);
            }
        }

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: text/html;charset=UTF-8\n\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);
        HttpMessageStreams responseMsg = HttpMessageStreams.of(responseStream);

        String pagePath = HttpRequestPagePath.of(httpRequestPath.getName()).path();
        InputStream pageIs = new FileInputStream(pagePath);
        StringStream pageStream = StringStream.of(pageIs);
        HttpMessageStream pageHtml = HttpMessageStream.of(pageStream);

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

    private String searchSessionId(HttpHeaderField cookie) {
        for(String value:cookie.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx),SessionStorage.SESSION_ID_NAME)) {
                return value.substring(delIdx+1);
            }
        }
        throw new NotFoundHttpHeadersPropertyException();
    }
}
