package com.request.handler;

import com.http.HttpMessageStreams;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.StringStream;
import com.db.dto.UserDto;
import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundQueryStringValueException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.db.table.Users;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HttpRequestUserCreator implements HttpRequestHandler {
    private static final Users USERS = new Users();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder queryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            queryString.append(bodyStream.generate());
        }

        String userIdValue = searchValue(queryString.toString(), "id");
        String userPwdValue = searchValue(queryString.toString(), "password");

        if (USERS.isAlreadyRegisteredName(userIdValue)) {
            return createRedirectionResponse(HttpResponseStatus.CODE_400);
        }

        UserDto newUser = UserDto.builder()
                .uid(UUID.randomUUID().toString())
                .name(userIdValue)
                .pwd(userPwdValue)
                .usageCapacity(0)
                .build();

        USERS.insert(newUser);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/login\n");

        return HttpMessageStreams.of(response.toString());
    }

    private String searchValue(String query, String target) {
        int startIdx = query.indexOf(target + "=");

        if (startIdx == -1) {
            throw new NotFoundQueryStringValueException();
        }

        int endIdx = query.indexOf("&", startIdx + target.length() + 1);

        if (endIdx != -1) {
            return URLDecoder.decode(
                    query.substring(startIdx + target.length() + 1, endIdx),
                    StandardCharsets.UTF_8);
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
