package com.request;

import com.method.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class StartLine {

    public enum Method {
        GET(new GetMethod()),
        POST(new PostMethod()),
        PUT(new PutMethod()),
        DELETE(new DeleteMethod());

        private final HttpMethod httpMethod;
        private static final Set<String> methods = createValues();

        private static Set<String> createValues() {
            Set<String> methods = new HashSet<>();

            for(Method h:values()) {
                methods.add(h.name());
            }

            return methods;
        }

        Method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public static boolean contains(String method) {
            return methods.contains(method);
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
        if (startLine == null || startLine.isBlank()) {
            throw new RuntimeException();
        }

        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        if (tokenizer.countTokens() != 3) {
            throw new RuntimeException();
        }

        String method = tokenizer.nextToken();
        Method httpMethod = Method.valueOf(method);

        if (!Method.contains(method)) {
            throw new RuntimeException();
        }

        String path = tokenizer.nextToken();

        if(!Objects.equals(path.charAt(0),'/')) {
            throw new RuntimeException();
        }

        if (Objects.equals(method,"GET")) {
            path = path.split("\\?")[0];
        }

        String version = tokenizer.nextToken();

        if(version.length() < 6 || !Objects.equals(version.substring(0,5),"HTTP/")) {
            throw new RuntimeException();
        }

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
