package com;

import com.http.HttpPropertyFinder;
import com.http.HttpPropertyKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

@Disabled
class HttpPropertyFinderTest {
    private static final Properties PROPERTIES = new Properties();
    private static final HttpPropertyFinder PROPERTY_FINDER = HttpPropertyFinder.getInstance();

    @BeforeAll
    static void loadProperties() throws IOException {
        PROPERTIES.load(new FileInputStream(Path.of("C:","dev","intellij","filedrive","http","http.properties").toFile()));
    }

    @Test
    @DisplayName("PropertyKey 로 값을 정상적으로 리턴하는지 테스트")
    void testWithHTTP_REQUEST_HEADERS_LENGTH_LIMIT() {
        //given
        String key = HttpPropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT.value();
        int expected = Integer.parseInt(PROPERTIES.getProperty(key));

        //when
        int actual = PROPERTY_FINDER.httpRequestHeadersLengthLimit();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("PropertyKey 로 값을 정상적으로 리턴하는지 테스트")
    void testWithHTTP_REQUEST_BODY_LENGTH_LIMIT() {
        //given
        String key = HttpPropertyKey.HTTP_REQUEST_BODY_LENGTH_LIMIT.value();
        int expected = Integer.parseInt(PROPERTIES.getProperty(key));

        //when
        int actual = PROPERTY_FINDER.httpRequestBodyLengthLimit();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("PropertyKey 로 값을 정상적으로 리턴하는지 테스트")
    void testWithNOT_ALLOWED_EXTENSION() {
        //given
        String key = HttpPropertyKey.NOT_ALLOWED_FILE_EXTENSION.value();
        String value = PROPERTIES.getProperty(key);
        Set<String> expected = Set.of(value.split(","));

        //when
        Set<String> actual = PROPERTY_FINDER.notAllowedFileExtension();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}