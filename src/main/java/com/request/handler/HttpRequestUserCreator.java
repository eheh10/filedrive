package com.request.handler;

import com.db.dto.UserDto;
import com.db.table.Users;
import com.http.HttpMessageStream;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotFoundQueryStringValueException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HttpRequestUserCreator implements HttpRequestHandler {
    private static final Users USERS = new Users();

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null || queryString == null || requestLengthLimiters == null) {
            throw new InputNullParameterException(
                    "httpRequestPath: "+httpRequestPath+"\n"+
                            "httpHeaders: "+httpHeaders+"\n"+
                            "bodyStream: "+bodyStream+"\n"+
                            "queryString: "+queryString+"\n"+
                            "requestLengthLimiters: "+requestLengthLimiters+"\n"
            );
        }

        StringBuilder bodyQueryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            bodyQueryString.append(bodyStream.generate());
        }

        String userIdValue = searchValue(bodyQueryString.toString(), "id");
        String userPwdValue = searchValue(bodyQueryString.toString(), "password");

        if (USERS.isAlreadyRegisteredName(userIdValue)) {
            throw new InvalidHttpRequestInputException();
        }

        UserDto newUser = UserDto.builder()
                .uid(UUID.randomUUID().toString())
                .name(userIdValue)
                .pwd(userPwdValue)
                .usageCapacity(0)
                .build();

        USERS.insert(newUser);

        HttpMessageStream responseHeaders = HttpMessageStream.of("Location: http://localhost:7777/page/login");

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_303,
                responseHeaders,
                HttpMessageStream.empty()
        );
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
