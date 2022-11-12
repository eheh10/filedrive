package com.request.handler;

import com.HttpMessageStream;
import com.HttpMessageStreams;
import com.HttpResponseStatus;
import com.StringStream;
import com.dto.UserDto;
import com.exception.InputNullParameterException;
import com.exception.NotFoundQueryStringValueException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.table.UserTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequestUserCreator implements HttpRequestHandler {
    private final UserTable userTable = new UserTable();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStream bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder queryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            queryString.append(bodyStream.generate());
        }

        String userIdValue = searchValue(queryString.toString(), "id");
        String userPwdValue = searchValue(queryString.toString(), "password");

        UserDto userDto = UserDto.builder()
                .id(userIdValue)
                .pwd(userPwdValue)
                .build();
        userTable.register(userDto);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/login\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);

        return HttpMessageStreams.of(responseStream);
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
