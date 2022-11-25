package com.http.request;

import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotAllowedHttpMethodException;
import com.http.exception.InputNullParameterException;

import java.util.Objects;
import java.util.StringTokenizer;

public class HttpRequestStartLine {

    private final HttpRequestMethod method;
    private final HttpRequestPath path;
    private final String version;


    private HttpRequestStartLine(HttpRequestMethod method, HttpRequestPath path, String version) {
        if (method==null) {
            throw new InputNullParameterException("StarLineParser.HttpRequestMethod is null");
        }
        if (path==null) {
            throw new InputNullParameterException("StarLineParser.path is null");
        }
        if (version==null) {
            throw new InputNullParameterException("StarLineParser.version is null");
        }

        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestStartLine parse(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new InvalidHttpRequestInputException();
        }

        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        if (tokenizer.countTokens() != 3) {
            throw new InvalidHttpRequestInputException("Invalid Http Request StartLine");
        }

        HttpRequestMethod method;
        try {
            method = HttpRequestMethod.valueOf(tokenizer.nextToken());
        } catch (IllegalArgumentException e) {
            throw new NotAllowedHttpMethodException();
        }

        String pathText = tokenizer.nextToken();

        if(!Objects.equals(pathText.charAt(0),'/')) {
            throw new InvalidHttpRequestInputException("Invalid Path In Http Request StartLine");
        }

        if (Objects.equals(method,HttpRequestMethod.GET)) {
            pathText = pathText.split("\\?")[0];
        }

        HttpRequestPath path = HttpRequestPath.of(pathText);

        String version = tokenizer.nextToken();

        if(version.length() < 6 || !Objects.equals(version.substring(0,5),"HTTP/")) {
            throw new InvalidHttpRequestInputException("Invalid Version In Http Request StartLine");
        }

        return new HttpRequestStartLine(method,path,version);
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public HttpRequestPath getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
