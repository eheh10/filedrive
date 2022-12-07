package com.api.property;

import com.api.exception.InputNullParameterException;
import com.api.exception.NotFoundPropertyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

public class ApiPropertyFinder {
    private final Properties value = new Properties();

    public ApiPropertyFinder() {
        try {
            value.load(new FileInputStream(Path.of("api","api.properties").toFile()));
        } catch (IOException e) {
            throw new NotFoundPropertyException("Not Found api.properties");
        }
    }

    private String find(ApiPropertyKey key) {
        if (key == null) {
            throw new InputNullParameterException();
        }
        return value.getProperty(key.value());
    }

    public String googleSingUpRedirectURI() {
        return find(ApiPropertyKey.GOOGLE_SIGN_UP_REDIRECT_URI);
    }

    public String googleLoginRedirectURI() {
        return find(ApiPropertyKey.GOOGLE_LOGIN_REDIRECT_URI);
    }

    public String googleAuthCodeUri(String redirectUri) {
        return new StringBuilder().append(find(ApiPropertyKey.GOOGLE_AUTH_CODE_URI))
                .append("&redirect_uri=").append(redirectUri)
                .toString();
    }

    public URI googleTokenUri() {
        return URI.create(find(ApiPropertyKey.GOOGLE_TOKEN_URI));
    }

    public String googleTokenBody(String authorizationCode, String redirectUri) {
        return new StringBuilder().append(find(ApiPropertyKey.GOOGLE_TOKEN_BODY))
                .append("&code=").append(authorizationCode)
                .append("&redirect_uri=").append(redirectUri)
                .toString();
    }

    public URI googleUserInfoUri() {
        return URI.create(find(ApiPropertyKey.GOOGLE_USER_INFO_URI));
    }
}
