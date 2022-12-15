package com.api.dto;

import com.api.exception.InputNullParameterException;
import lombok.Builder;
import lombok.ToString;

@ToString
public class TokenDto {
    private final String accessToken;
    private final String scope;
    private final String token_type;
    private final String id_token;
    private final String refresh_token;

    @Builder
    public TokenDto(String accessToken, String scope, String token_type, String id_token, String refresh_token) {
        if (accessToken==null || scope==null || token_type==null || id_token==null || refresh_token==null) {
            throw new InputNullParameterException(
                    "accessToken: "+accessToken+"\n"+
                    "scope: "+scope+"\n"+
                    "token_type: "+token_type+"\n"+
                    "id_token: "+id_token+"\n"+
                    "refresh_token: "+refresh_token+"\n"
            );
        }

        this.accessToken = accessToken;
        this.scope = scope;
        this.token_type = token_type;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getScope() {
        return scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getId_token() {
        return id_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
