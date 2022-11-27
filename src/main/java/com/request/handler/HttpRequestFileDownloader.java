package com.request.handler;

import com.db.dto.FileDto;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import com.http.exception.NotFoundHttpRequestFileException;
import com.http.exception.NotFoundQueryStringValueException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private static final Path DEFAULT_PATH = Paths.get("src","main","resources","uploaded-file");
    private final SessionStorage sessionStorage = new SessionStorage();
    private final UserFiles userFiles = new UserFiles();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder queryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            queryString.append(bodyStream.generate());
        }

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        String userUid = sessionStorage.getUserUid(sessionId);

        String fileName = searchFileName(queryString.toString(), "fileName");
        FileDto foundFile = userFiles.findFile(fileName,userUid);
        String targetPath = foundFile.getPath();
        File targetFile = DEFAULT_PATH.resolve(targetPath).toFile();

        if (!targetFile.exists()) {
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
            InputStream fileIs = new FileInputStream(targetFile);
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

    private String searchSessionId(HttpHeaderField cookie) {
        for(String value:cookie.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx), SessionStorage.SESSION_FIELD_NAME)) {
                return value.substring(delIdx+1);
            }
        }
        throw new NotFoundHttpHeadersPropertyException();
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
