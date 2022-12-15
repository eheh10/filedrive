package com.request.handler;

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
import com.request.HttpRequestPagePath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class HttpRequestPageStream implements HttpRequestHandler {

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

        HttpMessageStream responseHeaders = HttpMessageStream.of("Content-Type: text/html;charset=UTF-8");

        String pagePath = HttpRequestPagePath.of(httpRequestPath.getName()).path();
        InputStream pageIs = null;
        try {
            pageIs = new FileInputStream(pagePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        HttpMessageStream pageHtml = HttpMessageStream.of(pageIs);

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_200,
                responseHeaders,
                pageHtml
        );
    }
}
