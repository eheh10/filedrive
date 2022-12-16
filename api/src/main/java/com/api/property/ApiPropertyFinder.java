package com.api.property;

import com.api.exception.InputNullParameterException;
import com.api.exception.NotFoundPropertyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

public class ApiPropertyFinder {
    private static final String PROPERTY_FILE_PATH = Path.of("api","api.properties").toString();
    private static final ApiPropertyFinder INSTANCE = new ApiPropertyFinder(createProperties());
    private final Properties value;

    private ApiPropertyFinder(Properties value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public static ApiPropertyFinder getInstance() {
        return INSTANCE;
    }

    private static Properties createProperties() {
        try {
            Properties value = new Properties();
            value.load(new FileInputStream(PROPERTY_FILE_PATH));

            return value;
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
