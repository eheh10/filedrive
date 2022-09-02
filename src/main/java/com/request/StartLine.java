package com.request;

import com.method.*;

import java.util.Objects;
import java.util.StringTokenizer;

public class StartLine {

    private enum Method {
        GET(new GetMethod()),
        POST(new PostMethod()),
        PUT(new PutMethod()),
        DELETE(new DeleteMethod());

        private final HttpMethod httpMethod;

        Method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

    }

    private final Method httpMethod;
    private final String path;
    private final String version;


    private StartLine(Method httpMethod, String path, String version) {
        if (httpMethod==null) {
            throw new RuntimeException("StarLineParser.httpMethod is null");
        }
        if (path==null) {
            throw new RuntimeException("StarLineParser.path is null");
        }
        if (version==null) {
            throw new RuntimeException("StarLineParser.version is null");
        }

        this.httpMethod = httpMethod;
        this.path = path;
        this.version = version;
    }

    public static StartLine parse(String startLine) {
        StringTokenizer tokenizer = new StringTokenizer(startLine," ");

        String method = tokenizer.nextToken();
        Method httpMethod = Method.valueOf(method);

        String path = tokenizer.nextToken();
        if (Objects.equals(method,"GET")) {
            path = path.split("\\?")[0];
        }

        String version = tokenizer.nextToken();

        return new StartLine(httpMethod,path,version);
    }

    public Method getMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
