package com.request;

import java.util.Objects;
import java.util.StringTokenizer;

public class StartLine {

    private final String method;
    private final String path;
    private final String version;


    private StartLine(String method, String path, String version) {
        if (method==null) {
            throw new RuntimeException("StarLineParser.httpMethod is null");
        }
        if (path==null) {
            throw new RuntimeException("StarLineParser.path is null");
        }
        if (version==null) {
            throw new RuntimeException("StarLineParser.version is null");
        }

        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static StartLine parse(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new RuntimeException();
        }

        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        if (tokenizer.countTokens() != 3) {
            throw new RuntimeException();
        }

        String method = tokenizer.nextToken();

        String path = tokenizer.nextToken();

        if(!Objects.equals(path.charAt(0),'/')) {
            throw new RuntimeException();
        }
        if (Objects.equals(path,"/")) {
            path = "/default";
        }
        path = path.substring(1);

        if (Objects.equals(method,"GET")) {
            path = path.split("\\?")[0];
        }

        String version = tokenizer.nextToken();

        if(version.length() < 6 || !Objects.equals(version.substring(0,5),"HTTP/")) {
            throw new RuntimeException();
        }

        return new StartLine(method,path,version);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
