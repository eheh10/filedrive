package com.response;

import com.api.HttpRequestBodyFileCreator;
import com.api.HttpRequestHandlers;
import com.generator.HttpStringGenerator;
import com.method.HttpRequestMethod;
import com.path.HttpRequestPath;
import com.status.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class HttpRequestTransportTest {
    private static Method errorResponseMethod;

    private static HttpRequestHandlers handlers = new HttpRequestHandlers();

    private static Stream<String> provideTestRequest() throws Exception {
        URL url = HttpRequestTransport.class.getClassLoader().getResource("test-request.txt");
        return Stream.of(Files.readString(Paths.get(url.toURI())));
    }

    private String getErrorResponse(HttpStatus status) throws InvocationTargetException, IllegalAccessException {
        HttpRequestTransport transport = new HttpRequestTransport();
        return (String) errorResponseMethod.invoke(transport,status);
    }


    @BeforeAll
    static void setHandlers() throws NoSuchMethodException {
        errorResponseMethod = HttpRequestTransport.class.getDeclaredMethod("createHttpErrorResponse",HttpStatus.class);
        errorResponseMethod.setAccessible(true);
        handlers.register(HttpRequestPath.of("/test"), HttpRequestMethod.POST,new HttpRequestBodyFileCreator());
    }

    @ParameterizedTest
    @MethodSource("provideTestRequest")
    @DisplayName("유효한 Request 인 경우 응답코드 200 확인 테스트")
    void testStatusCodeWithNormalRequest(String testRequest) throws IOException {
        //given
        InputStream is = new ByteArrayInputStream(testRequest.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);
        HttpRequestTransport transport = new HttpRequestTransport();

        //when
        String response = transport.transport(generator,handlers);

        //then
        System.out.println(response);
        String startLine = response.split("\n")[0];
        Assertions.assertThat(startLine).contains("200");
    }

    @ParameterizedTest
    @MethodSource("provideTestRequest")
    @DisplayName("POST /test Request 시 파일 생성 테스트")
    void testPostMethodTestPath(String testRequest) throws IOException {
        //given
        InputStream is = new ByteArrayInputStream(testRequest.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);
        HttpRequestTransport transport = new HttpRequestTransport();

        //when
        String response = transport.transport(generator,handlers);

        //then
        Path filePath = Paths.get(System.getProperty("user.home"),"fileDrive","test.txt");
        System.out.println(Files.readString(filePath));
        Assertions.assertThat(filePath).exists();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "WRONG\n",
            "WRONG StartLine\n",
            "WRONG StartLine",
    })
    @DisplayName("유효하지 않은 Http Request StartLine 일때 statusCode 400 테스트")
    void testWithWrongStartLine(String request) throws InvocationTargetException, IllegalAccessException, IOException {
        //given
        String expected = getErrorResponse(HttpStatus.code400);
        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);
        HttpRequestTransport transport = new HttpRequestTransport();

        //when
        String actual = transport.transport(generator,handlers);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Http Request StartLine Method 가 지원하지않는 경우 statusCode 405 테스트")
    void testWithWrongStartLineMethod() throws InvocationTargetException, IllegalAccessException, IOException {
        //given
        String expected = getErrorResponse(HttpStatus.code405);
        String startLine = "WRONG /test HTTP/1.1\n";
        InputStream is = new ByteArrayInputStream(startLine.getBytes(StandardCharsets.UTF_8));
        HttpStringGenerator generator = HttpStringGenerator.of(is);
        HttpRequestTransport transport = new HttpRequestTransport();

        //when
        String actual = transport.transport(generator,handlers);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

//    @Test
//    @DisplayName("Http Request Headers 가 제한길이 초과했을때 statusCode 431 테스트")
//    void testWithWrongHeaders() throws InvocationTargetException, IllegalAccessException, IOException {
//        //given
//        String expected = getErrorResponse(HttpStatus.code400);
//        InputStream is = new ByteArrayInputStream(testRequest.getBytes(StandardCharsets.UTF_8));
//        HttpStringGenerator generator = HttpStringGenerator.of(is);
//        HttpRequestTransport transport = new HttpRequestTransport();
//
//        //when
//        String actual = transport.transport(generator,handlers);
//
//        //then
//        Assertions.assertThat(actual).isEqualTo(expected);
//    }

}