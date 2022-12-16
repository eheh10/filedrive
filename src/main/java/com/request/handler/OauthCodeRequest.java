package com.request.handler;

import com.api.property.ApiPropertyFinder;
import com.http.HttpMessageStream;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;

import java.util.Objects;


public class OauthCodeRequest implements HttpRequestHandler {
    private final ApiPropertyFinder propertyFinder = ApiPropertyFinder.getInstance();

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null || queryString == null || requestLengthLimiters == null) {
            throw new InputNullParameterException(
                    "httpRequestPath: "+httpRequestPath+"\n"+
                            "httpHeaders: "+httpHeaders+"\n"+
                            "bodyStream: "+bodyStream+"\n"+
                            "queryString: "+queryString+"\n"+
                            "requestLengthLimiters: "+requestLengthLimiters+"\n"
            );
        }

        String redirectUrl = "";
        if (Objects.equals(httpRequestPath.getName(),"login")) {
            redirectUrl = propertyFinder.googleLoginRedirectURI();
        }

        if (Objects.equals(httpRequestPath.getName(),"signUp")) {
            redirectUrl = propertyFinder.googleSingUpRedirectURI();
        }

        String codeUrl = propertyFinder.googleAuthCodeUri(redirectUrl);

        HttpMessageStream responseHeaders = HttpMessageStream.of("Location:"+codeUrl);

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_303,
                responseHeaders,
                HttpMessageStream.empty()
        );
    }
}
