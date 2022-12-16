package com.request.handler;

import com.api.GoogleApiRequest;
import com.api.dto.TokenDto;
import com.api.dto.UserInfoDto;
import com.api.property.ApiPropertyFinder;
import com.db.dto.UserDto;
import com.db.table.SessionStorage;
import com.db.table.Users;
import com.http.HttpMessageStream;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;

public class OauthUserFinder implements HttpRequestHandler {
    private static final Users USERS = new Users();
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private final ApiPropertyFinder propertyFinder = ApiPropertyFinder.getInstance();
    private final GoogleApiRequest googleApiRequest = new GoogleApiRequest();


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

        String authorizationCode = queryString.getFieldValue("code");

        String redirectUri = propertyFinder.googleLoginRedirectURI();
        String accessTokenRequestBody = propertyFinder.googleTokenBody(authorizationCode,redirectUri);

        TokenDto tokenDto = googleApiRequest.requestToken(accessTokenRequestBody);
        String accessToken = tokenDto.getAccessToken();

        UserInfoDto userInfoDto = googleApiRequest.requestUserInfo(accessToken);
        String googleUid = userInfoDto.getSnsUid();

        UserDto loginUser = USERS.searchByGoogleUid(googleUid);
        if (loginUser == null) {
            throw new InvalidHttpRequestInputException();
        }

        String sessionId = SESSION_STORAGE.createSession(loginUser);

        StringBuilder headers = new StringBuilder();
        headers.append("Location: http://localhost:7777/page/upload\n")
                .append("Set-Cookie: ").append(SessionStorage.SESSION_FIELD_NAME).append("=").append(sessionId)
                .append("Set-Cookie: ").append("access_token").append("=").append(tokenDto.getAccessToken())
                .append("access_token=").append(tokenDto.getAccessToken());
        HttpMessageStream responseHeaders = HttpMessageStream.of(headers.toString());

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_200,
                responseHeaders,
                HttpMessageStream.empty()
        );
    }

}
