package com.api;

import com.exception.NullException;
import com.generator.HttpStringGenerator;
import com.request.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HttpRequestBodyFileCreatorTest {

    @Test
    @DisplayName("파일 생성 테스트")
    void testFileCreated() throws IOException {
        //given
        String expected = "Hello World";
        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);

        //when
        testHandler.handle(httpHeaders,generator);
        Path filePath = Paths.get(System.getProperty("user.home"),"fileDrive","test.txt");

        //then
        Assertions.assertThat(filePath).exists();
    }

    @Test
    @DisplayName("HttpStringGenerator 내용이 파일에 작성되는지 테스트")
    void testFileContent() throws IOException {
        //given
        String expected = "Hello World";
        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);

        //when
        testHandler.handle(httpHeaders,generator);
        Path filePath = Paths.get(System.getProperty("user.home"),"fileDrive","test.txt");
        String actual = Files.readString(filePath);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("HttpHeaders,HttpStringGenerator 가 null 일때 예외 발생 테스트")
    void testHandleWithNull() throws IOException {
        //given
        String expected = "Hello World";
        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);

        //when
        Assertions.assertThatThrownBy(()->testHandler.handle(null,generator))
                .isInstanceOf(NullException.class);

        Assertions.assertThatThrownBy(()->testHandler.handle(httpHeaders,null))
                .isInstanceOf(NullException.class);
    }

}