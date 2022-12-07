package com.request.handler;

import com.api.property.ApiPropertyFinder;
import com.http.HttpMessageStreams;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;

import java.util.Objects;


public class OauthCodeRequest implements HttpRequestHandler {
    private final ApiPropertyFinder propertyFinder = new ApiPropertyFinder();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        String redirectUrl = "";
        if (Objects.equals(httpRequestPath.getName(),"login")) {
            redirectUrl = propertyFinder.googleLoginRedirectURI();
        }

        if (Objects.equals(httpRequestPath.getName(),"singUp")) {
            redirectUrl = propertyFinder.googleSingUpRedirectURI();
        }

        String codeUrl = propertyFinder.googleAuthCodeUri(redirectUrl);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location:").append(codeUrl).append("\n");

        return HttpMessageStreams.of(response.toString());
    }
}
