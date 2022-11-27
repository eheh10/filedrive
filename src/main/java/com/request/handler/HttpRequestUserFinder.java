package com.request.handler;

import com.http.HttpMessageStreams;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.db.dto.UserDto;
import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotFoundQueryStringValueException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.db.table.SessionStorage;
import com.db.table.Users;

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

        UserDto loginUser = users.searchByNamePwd(userIdValue,userPwdValue);
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
