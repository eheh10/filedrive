package com.api.property;

import com.api.exception.InputNullParameterException;

public enum ApiPropertyKey {
    GOOGLE_SIGN_UP_REDIRECT_URI("google_sign_up_redirect_uri"),
    GOOGLE_LOGIN_REDIRECT_URI("google_login_redirect_uri"),
    GOOGLE_AUTH_CODE_URI("google_auth_code_uri"),
    GOOGLE_TOKEN_URI("google_token_uri"),
    GOOGLE_TOKEN_BODY("google_token_body"),
    GOOGLE_USER_INFO_URI("google_user_info_uri");

    private final String value;
    ApiPropertyKey(String value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public String value() {
        return value;
    }
}
