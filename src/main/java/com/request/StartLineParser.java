package com.request;

import com.method.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class StartLineParser {

    private enum Method {
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

    private final HttpMethod httpMethod;
    private final String path;
    private final String version;


    private StartLineParser(HttpMethod httpMethod, String path, String version) {
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

    public static StartLineParser of(String startLine) {
        StringTokenizer tokenizer = new StringTokenizer(startLine," ");

        String method = tokenizer.nextToken();
        HttpMethod httpMethod = Method.valueOf(method).getHttpMethod();

        String path = tokenizer.nextToken();
        if (Objects.equals(method,"GET")) {
            path = path.split("\\?")[0];
        }

        String version = tokenizer.nextToken();

        return new StartLineParser(httpMethod,path,version);
    }

    public static boolean isValidDataStructure(String startLine) {
        StringTokenizer tokenizer = new StringTokenizer(startLine," ");

        if (tokenizer.countTokens() != 3) {
            return false;
        }

        String method = tokenizer.nextToken();
        if (!Method.contains(method)) {
            return false;
        }

        String path = tokenizer.nextToken();
        if(!Objects.equals(path.charAt(0),'/')) {
            return false;
        }

        String version = tokenizer.nextToken();
        if(!Objects.equals(version.substring(0,5),"HTTP/")) {
            return false;
        }

        return true;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
