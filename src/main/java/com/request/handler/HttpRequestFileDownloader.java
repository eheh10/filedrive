package com.request.handler;

import com.*;
import com.exception.InputNullParameterException;
import com.exception.NotFoundHttpRequestFileException;
import com.exception.NotFoundQueryStringValueException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.response.HttpResponseStatus;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private static final Path DEFAULT_PATH = Paths.get("src","main","resources","uploaded-file");

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder queryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            queryString.append(bodyStream.generate());
        }

        String fileName = searchFileName(queryString.toString(), "fileName");
        Path targetPath = DEFAULT_PATH.resolve(fileName);

        if (Files.notExists(targetPath)) {
            throw new NotFoundHttpRequestFileException();
        }

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: application/octet-stream;charset=euc-kr\n")
                .append("Content-Disposition: attachment; filename=\"")
                .append(fileName)
                .append("\"\n\n");

        HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());

        try {
            InputStream fileIs = new FileInputStream(targetPath.toFile());
            HttpMessageStream responseBody = HttpMessageStream.of(fileIs);

            return responseMsg.sequenceOf(responseBody);
        } catch (FileNotFoundException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
        }
    }

    private String searchFileName(String query, String target) {
        int startIdx = query.indexOf(target + "=");

        if (startIdx == -1) {
            throw new NotFoundQueryStringValueException();
        }

        return URLDecoder.decode(
                query.substring(startIdx + target.length() + 1),
                StandardCharsets.UTF_8);
    }

    private HttpMessageStreams createRedirectionResponse(HttpResponseStatus status) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(status.code()).append(" ")
                .append(status.message()).append("\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);

        return HttpMessageStreams.of(responseStream);
    }
}
