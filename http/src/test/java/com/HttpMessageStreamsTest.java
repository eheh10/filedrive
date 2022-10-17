package com;

import com.exception.InputNullParameterException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class HttpMessageStreamsTest {

    @Test
    @DisplayName("복수 HttpMessageStreams 연결 테스트")
    void testSequenceOfWithHttpMessageStreams() throws IOException {
        //given
        String txt1 = "Hello";
        String txt2 = "World";
        String expected = txt1 + txt2;
        InputStream is1 = new ByteArrayInputStream(txt1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(txt2.getBytes(StandardCharsets.UTF_8));
        HttpMessageStreams streams1 = HttpMessageStreams.of(StringStream.of(is1));
        HttpMessageStreams streams2 = HttpMessageStreams.of(StringStream.of(is2));

        //then
        HttpMessageStreams generator = streams1.sequenceOf(streams2);
        StringBuilder actual = new StringBuilder();
        while (generator.hasMoreString()) {
            actual.append(generator.generate());
        }

        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("복수 HttpMessageStream 연결 테스트")
    void testSequenceOfWithHttpMessageStream() throws IOException {
        //given
        String txt1 = "Hello";
        String txt2 = "World";
        String expected = txt1 + txt2;
        InputStream is1 = new ByteArrayInputStream(txt1.getBytes(StandardCharsets.UTF_8));
        InputStream is2 = new ByteArrayInputStream(txt2.getBytes(StandardCharsets.UTF_8));
        HttpMessageStream stream1 = HttpMessageStream.of(StringStream.of(is1));
        HttpMessageStream stream2 = HttpMessageStream.of(StringStream.of(is2));
        HttpMessageStreams streams = HttpMessageStreams.empty().sequenceOf(stream1).sequenceOf(stream2);

        //then
        StringBuilder actual = new StringBuilder();
        while (streams.hasMoreString()) {
            actual.append(streams.generate());
        }

        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> HttpMessageStreams.of(null))
                .isInstanceOf(InputNullParameterException.class);
    }

    @Test
    @DisplayName("null 로 sequenceOf() 호출시 에러 발생 테스트")
    void testSequenceOfWithNull() {
        //given
        String str = "Hello";
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        StringStream isGenerator = StringStream.of(is);
        HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);

        Assertions.assertThatThrownBy(()-> generator.sequenceOf((HttpMessageStream) null))
                .isInstanceOf(InputNullParameterException.class);
        Assertions.assertThatThrownBy(()-> generator.sequenceOf((HttpMessageStreams) null))
                .isInstanceOf(InputNullParameterException.class);
    }

}