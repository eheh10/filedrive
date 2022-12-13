package com;

import com.http.HttpMessageStream;
import com.http.HttpMessageStreams;
import com.http.ResourceStream;
import com.http.exception.InputNullParameterException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class HttpMessageStreamsTest {

    @Test
    @DisplayName("복수 HttpMessageStream 연결 테스트")
    void testSequenceOf() {
        //given
        String txt1 = "Hello";
        String txt2 = "World";
        String expected = txt1 + txt2;
        HttpMessageStreams streams1 = HttpMessageStreams.of(txt1);
        HttpMessageStreams streams2 = HttpMessageStreams.of(txt2);
        HttpMessageStreams sequenceStreams1 = streams1.sequenceOf(streams2);

        HttpMessageStream stream1 = HttpMessageStream.of(txt1);
        HttpMessageStream stream2 = HttpMessageStream.of(txt2);
        HttpMessageStreams sequenceStreams2 = HttpMessageStreams.empty().sequenceOf(stream1).sequenceOf(stream2);

        //when
        StringBuilder actual1 = new StringBuilder();
        while (sequenceStreams1.hasMoreString()) {
            actual1.append(sequenceStreams1.generateString());
        }

        StringBuilder actual2 = new StringBuilder();
        while (sequenceStreams2.hasMoreString()) {
            actual2.append(sequenceStreams2.generateString());
        }

        //then
        Assertions.assertThat(actual1.toString()).isEqualTo(expected);
        Assertions.assertThat(actual2.toString()).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> HttpMessageStreams.of((String) null))
                .isInstanceOf(InputNullParameterException.class);
    }

    @Test
    @DisplayName("null 로 sequenceOf() 호출시 에러 발생 테스트")
    void testSequenceOfWithNull() {
        //given
        String str = "Hello";
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        ResourceStream isGenerator = ResourceStream.of(is);
        HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);

        Assertions.assertThatThrownBy(()-> generator.sequenceOf((HttpMessageStream) null))
                .isInstanceOf(InputNullParameterException.class);
        Assertions.assertThatThrownBy(()-> generator.sequenceOf((HttpMessageStreams) null))
                .isInstanceOf(InputNullParameterException.class);
    }

}