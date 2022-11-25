//package com.request.handler;
//
//import com.HttpMessageStreams;
//import com.StringStream;
//import com.exception.InputNullParameterException;
//import com.header.HttpHeaders;
//import com.request.HttpRequestPath;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//class HttpRequestBodyFileCreatorTest {
//    @Test
//    @DisplayName("파일 생성 테스트")
//    void testFileCreated() throws IOException {
//        //given
//        String expected = "Hello World";
//        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
//        HttpRequestPath httpRequestPath = HttpRequestPath.of("");
//        HttpHeaders httpHeaders = HttpHeaders.empty();
//        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
//        StringStream isGenerator = StringStream.of(is);
//        HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);
//
//        //when
//        testHandler.handle(httpRequestPath, httpHeaders,generator);
//        Path filePath = Paths.get(System.getProperty("user.home"),"fileDrive","test.txt");
//        String actual = Files.readString(filePath);
//
//        //then
//        Assertions.assertThat(filePath).exists();
//        Assertions.assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("HttpHeaders,HttpStringGenerator 가 null 일때 예외 발생 테스트")
//    void testHandleWithNull() {
//        //given
//        String expected = "Hello World";
//        HttpRequestBodyFileCreator testHandler = new HttpRequestBodyFileCreator();
//        HttpRequestPath httpRequestPath = HttpRequestPath.of("");
//        HttpHeaders httpHeaders = HttpHeaders.empty();
//        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
//        StringStream isGenerator = StringStream.of(is);
//        HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);
//
//        //when
//        Assertions.assertThatThrownBy(()->testHandler.handle(httpRequestPath,null,generator))
//                .isInstanceOf(InputNullParameterException.class);
//
//        Assertions.assertThatThrownBy(()->testHandler.handle(httpRequestPath,httpHeaders,null))
//                .isInstanceOf(InputNullParameterException.class);
//    }
//
//}