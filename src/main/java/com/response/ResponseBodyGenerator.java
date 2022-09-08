package com.response;

import com.api.HttpApi;
import com.path.HttpRequestPaths;
import com.request.BodyLineGenerator;
import com.request.HttpHeaders;
import com.request.StartLine;

import java.io.IOException;
import java.util.Objects;

public class ResponseBodyGenerator {
    private final StartLine startLine;
    private final HttpHeaders httpHeaders;
    private final BodyLineGenerator bodyLineGenerator;

    public ResponseBodyGenerator(StartLine startLine, HttpHeaders httpHeaders, BodyLineGenerator bodyLineGenerator) {
        this.startLine = startLine;
        this.httpHeaders = httpHeaders;
        this.bodyLineGenerator = bodyLineGenerator;
    }

    public String generate() throws IOException {
        String path = startLine.getPath();
        String method = startLine.getMethod();

        HttpApi httpApi = HttpRequestPaths.valueOf(path).binding();

        if (Objects.equals(method,"GET")) {
            return httpApi.get();
        }

        if (Objects.equals(method,"POST")) {
            return httpApi.post(bodyLineGenerator);
        }

        throw new RuntimeException();
    }
}
