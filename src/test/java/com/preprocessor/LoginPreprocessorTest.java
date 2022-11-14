package com.preprocessor;

import com.*;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.response.HttpResponseStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class LoginPreprocessorTest {
    private final LoginPreprocessor loginPreprocessor = new LoginPreprocessor();
    private final SessionStorage sessionStorage = new SessionStorage();

    private HttpHeaders createHeaders(String headers) throws IOException {
        InputStream is = new ByteArrayInputStream(headers.getBytes(StandardCharsets.UTF_8));
        StringStream stringStream = StringStream.of(is);
        HttpRequestStreamDecorator requestStream = new HttpRequestStreamDecorator(HttpMessageStream.of(stringStream));
        return HttpHeaders.parse(requestStream);
    }

    private String createResponseMsg(HttpResponseStatus status) {
        StringBuilder responseMsg = new StringBuilder();

        responseMsg.append("HTTP/1.1 ")
                .append(status.code()).append(" ")
                .append(status.message()).append("\n");

        return responseMsg.toString();
    }

    @DisplayName("유효한 세션 아이디로 요청이 들어온 경우 테스트")
    @Test
    public void testLoginWithValidSessionId() throws IOException {
        //given
        String expected = "";
        String sessionId = sessionStorage.createSession();
        HttpRequestPath httpRequestPath = HttpRequestPath.of("/test");
        String cookie = "Cookie:sessionId="+sessionId+"\n";
        HttpHeaders httpHeaders = createHeaders(cookie);

        //when
        HttpMessageStreams response = loginPreprocessor.process(httpRequestPath,httpHeaders);

        StringBuilder actual = new StringBuilder();
        while (response.hasMoreString()) {
            actual.append(response.generate());
        }

        //then
        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @DisplayName("헤더 쿠키에 세션 아이디 없이 요청했을때 401 응답 테스트트")
    @Test
    public void testLoginWithNoSessionId() throws IOException {
        //given
        String expected = createResponseMsg(HttpResponseStatus.CODE_401);
        HttpRequestPath httpRequestPath = HttpRequestPath.of("/test");
        String cookie = "Cookie:sessionID\n";
        HttpHeaders httpHeaders = createHeaders(cookie);

        //when
        HttpMessageStreams response = loginPreprocessor.process(httpRequestPath,httpHeaders);

        StringBuilder actual = new StringBuilder();
        while (response.hasMoreString()) {
            actual.append(response.generate());
        }

        //then
        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @DisplayName("필터 대상 URI 인 경우 빈메시지 리턴 테스트")
    @Test
    public void testFilter() throws IOException {
        //given
        String expected = "";
        HttpRequestPath httpRequestPath = HttpRequestPath.of("/filter");
        HttpHeaders httpHeaders = HttpHeaders.empty();

        //when
        loginPreprocessor.filter(httpRequestPath);
        HttpMessageStreams response = loginPreprocessor.process(httpRequestPath,httpHeaders);

        StringBuilder actual = new StringBuilder();
        while (response.hasMoreString()) {
            actual.append(response.generate());
        }

        //then
        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @DisplayName("파라미터가 null 일때 예외 발생 테스트")
    @Test
    void testProcessWithNull() {
        //given
        HttpRequestPath httpRequestPath = HttpRequestPath.of("/");
        HttpHeaders httpHeaders = HttpHeaders.empty();

        //when
        Assertions.assertThatThrownBy(()->loginPreprocessor.process(null,httpHeaders))
                .isInstanceOf(InputNullParameterException.class);

        Assertions.assertThatThrownBy(()->loginPreprocessor.process(httpRequestPath,null))
                .isInstanceOf(InputNullParameterException.class);
    }

}