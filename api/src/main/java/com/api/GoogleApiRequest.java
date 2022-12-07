package com.api;

import com.api.dto.TokenDto;
import com.api.dto.UserInfoDto;
import com.api.property.ApiPropertyFinder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class GoogleApiRequest {
    private final ApiPropertyFinder propertyFinder = new ApiPropertyFinder();
    private final HttpClient client = HttpClient.newBuilder().build();

    public TokenDto requestToken(String requiredQueryString) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requiredQueryString))
                .uri(propertyFinder.googleTokenUri())
                .setHeader("content-type","application/x-www-form-urlencoded")
                .build();

        HttpResponse<InputStream> tokenResponse;
        HashMap<String,Object> tokenJson;

        try {
            tokenResponse = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (tokenResponse.statusCode() != 200) {
                throw new RuntimeException();
            }

            tokenJson = new JsonMapper().readValue(tokenResponse.body(), HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String accessToken = (String) tokenJson.get("access_token");
        String scope = (String) tokenJson.get("scope");
        String tokenType = (String) tokenJson.get("token_type");
        String idToken = (String) tokenJson.get("id_token");
        String refreshToken = (String) tokenJson.get("refresh_token");

        return TokenDto.builder()
                .accessToken(accessToken)
                .token_type(tokenType)
                .id_token(idToken)
                .refresh_token(refreshToken)
                .scope(scope)
                .build();
    }

    public UserInfoDto requestUserInfo(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(propertyFinder.googleUserInfoUri())
                .setHeader("Authorization","Bearer "+accessToken)
                .build();

        HashMap<String,Object> infoJson;
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }

            infoJson = new JsonMapper().readValue(response.body(), HashMap.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String googleUid = (String) infoJson.get("id");
        String googleEmail = (String) infoJson.get("email");

        return UserInfoDto.builder()
                .snsUid(googleUid)
                .snsEmail(googleEmail)
                .build();
    }
}
