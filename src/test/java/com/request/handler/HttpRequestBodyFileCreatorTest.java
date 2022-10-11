package com.request.handler;

import com.HttpLengthLimiter;
import com.HttpsStream;
import com.StringStream;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.handler.HttpRequestBodyFileCreator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HttpRequestBodyFileCreatorTest {
    private final HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(2_097_152);
    @Test
    @DisplayName("파일 생성 테스트")
    void testFileCreated() throws IOException {
        //given
        String expected = "Hello World";
        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        StringStream isGenerator = StringStream.of(is);
        HttpsStream generator = HttpsStream.of(isGenerator);

        //when
        testHandler.handle(httpHeaders,generator, requestBodyLengthLimit);
        Path filePath = Paths.get(System.getProperty("user.home"),"fileDrive","test.txt");
        String actual = Files.readString(filePath);

        //then
        Assertions.assertThat(filePath).exists();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("HttpHeaders,HttpStringGenerator 가 null 일때 예외 발생 테스트")
    void testHandleWithNull() {
        //given
        String expected = "Hello World";
        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        StringStream isGenerator = StringStream.of(is);
        HttpsStream generator = HttpsStream.of(isGenerator);

        //when
        Assertions.assertThatThrownBy(()->testHandler.handle(null,generator, requestBodyLengthLimit))
                .isInstanceOf(InputNullParameterException.class);

        Assertions.assertThatThrownBy(()->testHandler.handle(httpHeaders,null, requestBodyLengthLimit))
                .isInstanceOf(InputNullParameterException.class);
    }

}