package com.request.handler;

import com.HttpMessageStreams;
import com.HttpRequestLengthLimiters;
import com.RetryHttpRequestStream;
import com.dto.UserDto;
import com.exception.InputNullParameterException;
import com.exception.InvalidHttpRequestInputException;
import com.exception.NotFoundQueryStringValueException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.response.HttpResponseStatus;
import com.table.SessionStorage;
import com.table.Users;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequestUserFinder implements HttpRequestHandler {
    private final Users users = new Users();
    private final SessionStorage sessionStorage = new SessionStorage();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder response = new StringBuilder();
        StringBuilder queryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            queryString.append(bodyStream.generate());
        }

        String userIdValue = searchValue(queryString.toString(), "id");
        String userPwdValue = searchValue(queryString.toString(), "password");

        UserDto loginUser = users.find_BY_ID_PWD(userIdValue,userPwdValue);
        if (loginUser == null) {
            throw new InvalidHttpRequestInputException();
        }

        String sessionId = sessionStorage.createSession(loginUser);

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/upload\n")
                .append("Set-Cookie: ").append(SessionStorage.SESSION_FIELD_NAME).append("=").append(sessionId)
                .append("\n");

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
}
