package com.request.handler;

import com.HttpMessageStream;
import com.HttpMessageStreams;
import com.StringStream;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import request.handler.HttpResourceStream;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HttpResourceStreamTest {

    private HttpMessageStreams getResponse(String file) throws IOException {
        Path filePath = Paths.get("src","test","resources","images",file);

        String headers = "Content-Type: "+ Files.probeContentType(filePath)+"\n\n";
        InputStream headerInput = new ByteArrayInputStream(headers.getBytes(StandardCharsets.UTF_8));
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpMessageStreams responseHeaders = HttpMessageStreams.of(headerGenerator);

        StringStream stringStream = StringStream.of(new FileInputStream(filePath.toString()));
        HttpMessageStream fileStreams = HttpMessageStream.of(stringStream);

        return responseHeaders.sequenceOf(fileStreams);
    }

    @DisplayName("정상적으로 파일을 찾는지 테스트")
    @ParameterizedTest()
    @ValueSource(strings = {"favicon.ico"})
    void testHandleResponse(String file) throws IOException {
        //given
        HttpMessageStreams expected = getResponse(file);

        HttpRequestPath requestPath = HttpRequestPath.of(file);
        HttpHeaders httpHeaders = HttpHeaders.empty();
        HttpMessageStreams streams = HttpMessageStreams.empty();
        HttpResourceStream httpResourceStream = new HttpResourceStream();

        //when
        HttpMessageStreams actual = httpResourceStream.handle(requestPath, httpHeaders,streams);

        //then
        while(actual.hasMoreString()) {
            Assertions.assertThat(expected.generate()).isEqualTo(actual.generate());
        }
    }

    @Test
    @DisplayName("파라미터가 null 일때 예외 발생 테스트")
    void testHandleWithNull() {
        //given
        HttpResourceStream testHandler = new HttpResourceStream();
        HttpRequestPath httpRequestPath = HttpRequestPath.of("/");
        HttpHeaders httpHeaders = HttpHeaders.empty();
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        StringStream isGenerator = StringStream.of(is);
        HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);

        //when
        Assertions.assertThatThrownBy(()->testHandler.handle(null,httpHeaders,generator))
                .isInstanceOf(InputNullParameterException.class);

        Assertions.assertThatThrownBy(()->testHandler.handle(httpRequestPath,null,generator))
                .isInstanceOf(InputNullParameterException.class);

        Assertions.assertThatThrownBy(()->testHandler.handle(httpRequestPath,httpHeaders,null))
                .isInstanceOf(InputNullParameterException.class);
    }
}