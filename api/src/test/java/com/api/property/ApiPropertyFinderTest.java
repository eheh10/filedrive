package com.api.property;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

class ApiPropertyFinderTest {
    private static final Properties PROPERTIES = new Properties();
    private static final ApiPropertyFinder PROPERTY_FINDER = ApiPropertyFinder.getInstance();

    @BeforeAll
    static void loadProperties() throws IOException {
        PROPERTIES.load(new FileInputStream(Path.of("C:","dev","intellij","filedrive","api","api.properties").toFile()));
    }

    @Test
    @DisplayName("GOOGLE_SIGN_UP_REDIRECT_URI property 테스트")
    void testOfGoogleSignUpRedirectUri() {
        //given
        String key = ApiPropertyKey.GOOGLE_SIGN_UP_REDIRECT_URI.value();
        String expected = PROPERTIES.getProperty(key);

        //when
        String actual = PROPERTY_FINDER.googleSingUpRedirectURI();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GOOGLE_LOGIN_REDIRECT_URI property 테스트")
    void testOfGoogleLoginRedirectUri() {
        //given
        String key = ApiPropertyKey.GOOGLE_LOGIN_REDIRECT_URI.value();
        String expected = PROPERTIES.getProperty(key);

        //when
        String actual = PROPERTY_FINDER.googleLoginRedirectURI();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GOOGLE_AUTH_CODE_URI property 테스트")
    void testOfGoogleAuthCodeUri() {
        //given
        String key = ApiPropertyKey.GOOGLE_AUTH_CODE_URI.value();
        String redirectUri = "http://test.com";
        String expected = PROPERTIES.getProperty(key)+
                "&redirect_uri="+redirectUri;

        //when
        String actual = PROPERTY_FINDER.googleAuthCodeUri(redirectUri);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GOOGLE_TOKEN_URI property 테스트")
    void testOfGoogleTokenUri() {
        //given
        String key = ApiPropertyKey.GOOGLE_TOKEN_URI.value();
        URI expected = URI.create(PROPERTIES.getProperty(key));

        //when
        URI actual = PROPERTY_FINDER.googleTokenUri();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GOOGLE_TOKEN_BODY property 테스트")
    void testOfGoogleTokenBody() {
        //given
        String authCode = "testAuthCode";
        String redirectUri = "http://test.com";
        String key = ApiPropertyKey.GOOGLE_TOKEN_BODY.value();
        String expected = PROPERTIES.getProperty(key)+
                "&code="+authCode+
                "&redirect_uri="+redirectUri;

        //when
        String actual = PROPERTY_FINDER.googleTokenBody(authCode,redirectUri);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GOOGLE_USER_INFO_URI property 테스트")
    void testOfGoogleUserInfoUri() {
        //given
        String key = ApiPropertyKey.GOOGLE_USER_INFO_URI.value();
        URI expected = URI.create(PROPERTIES.getProperty(key));

        //when
        URI actual = PROPERTY_FINDER.googleUserInfoUri();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}