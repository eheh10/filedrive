package com;

import com.http.ResourceStream;
import com.http.exception.InputNullParameterException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ResourceStreamTest {
    @ParameterizedTest
    @ValueSource(strings = {"Hello","Hello World\n","Hello\n World\n\n"})
    @DisplayName("끝까지 데이터를 읽는지 테스트")
    void testGenerate(String expected) {
        //given
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        ResourceStream generator = ResourceStream.of(is);

        //when
        StringBuilder actual = new StringBuilder();
        while(generator.hasMoreString()) {
            actual.append(generator.generateString());
        }

        //then
        Assertions.assertThat(actual.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello","Hello World\n","Hello\n World\n\n"})
    @DisplayName("개행문자까지만 데이터를 읽는지 테스트")
    void testGenerateLine(String str) {
        //given
        String expected = str.split("\n")[0];
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        ResourceStream generator = ResourceStream.of(is);

        //when
        String actual = generator.generateLine();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("읽을 값이 더 있는 경우 hasMoreString 테스트")
    void testHasMoreStringWithData() {
        //given
        boolean expected = true;
        InputStream is = new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));
        ResourceStream generator = ResourceStream.of(is);

        //when
        boolean actual = generator.hasMoreString();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("읽을 값이 없는 경우 hasMoreString 테스트")
    void testHasMoreStringWithNoData() {
        //given
        boolean expected = false;
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        ResourceStream generator = ResourceStream.of(is);

        //when
        boolean actual = generator.hasMoreString();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> ResourceStream.of(null))
                .isInstanceOf(InputNullParameterException.class);
    }
}