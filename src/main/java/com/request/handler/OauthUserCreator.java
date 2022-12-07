package com.request.handler;

import com.api.GoogleApiRequest;
import com.api.dto.TokenDto;
import com.api.dto.UserInfoDto;
import com.api.property.ApiPropertyFinder;
import com.db.dto.UserDto;
import com.db.table.Users;
import com.http.HttpMessageStreams;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;

import java.util.UUID;

public class OauthUserCreator implements HttpRequestHandler {
    private static final Users USERS = new Users();
    private final ApiPropertyFinder propertyFinder = new ApiPropertyFinder();
    private final GoogleApiRequest googleApiRequest = new GoogleApiRequest();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        String authorizationCode = queryString.getFieldValue("code");

        String redirectUri = propertyFinder.googleSingUpRedirectURI();
        String accessTokenRequestBody = propertyFinder.googleTokenBody(authorizationCode,redirectUri);

        TokenDto tokenDto = googleApiRequest.requestToken(accessTokenRequestBody);
        String accessToken = tokenDto.getAccessToken();

        UserInfoDto userInfoDto = googleApiRequest.requestUserInfo(accessToken);
        String googleUid = userInfoDto.getSnsUid();
        String googleEmail = userInfoDto.getSnsEmail();

        if (USERS.isAlreadyRegisteredName(googleEmail)) {
            throw new InvalidHttpRequestInputException();
        }

        UserDto newUser = UserDto.builder()
                .uid(UUID.randomUUID().toString())
                .name(googleEmail)
                .pwd(null)
                .usageCapacity(0)
                .googleUid(googleUid)
                .build();

        USERS.insert(newUser);

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/login\n");

        return HttpMessageStreams.of(response.toString());
    }

}
