package com.request;

import com.exception.InvalidValueException;
import com.exception.NotAllowedHttpMethodException;
import com.exception.NullException;
import com.method.HttpRequestMethod;
import com.path.HttpRequestPath;

import java.util.Objects;
import java.util.StringTokenizer;

public class StartLine {

    private final HttpRequestMethod method;
    private final HttpRequestPath path;
    private final String version;


    private StartLine(HttpRequestMethod method, HttpRequestPath path, String version) {
        if (method==null) {
            throw new NullException("StarLineParser.HttpRequestMethod is null");
        }
        if (path==null) {
            throw new NullException("StarLineParser.path is null");
        }
        if (version==null) {
            throw new NullException("StarLineParser.version is null");
        }

        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static StartLine parse(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new NullException();
        }

        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        if (tokenizer.countTokens() != 3) {
            throw new InvalidValueException();
        }

        HttpRequestMethod method;
        try {
            method = HttpRequestMethod.valueOf(tokenizer.nextToken());
        } catch (IllegalArgumentException e) {
            throw new NotAllowedHttpMethodException();
        }

        String pathText = tokenizer.nextToken();

        if(!Objects.equals(pathText.charAt(0),'/')) {
            throw new InvalidValueException();
        }

        if (Objects.equals(method,"GET")) {
            pathText = pathText.split("\\?")[0];
        }

        HttpRequestPath path = HttpRequestPath.of(pathText);

        String version = tokenizer.nextToken();

        if(version.length() < 6 || !Objects.equals(version.substring(0,5),"HTTP/")) {
            throw new InvalidValueException();
        }

        return new StartLine(method,path,version);
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
