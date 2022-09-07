package com.request;

import com.exception.NullException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.charset.StandardCharsets;

class BodyLineGeneratorTest {

    private BufferedReader getBufferedReader(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);
        return br;
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello world","Hello\nWorld", "  "})
    @DisplayName("한번 읽기(버퍼크기만큼) 테스트")
    void testGenerateOneLine(String expected) throws IOException {
        //given
        BufferedReader br = getBufferedReader(expected);
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);

        //when
        String actual = bodyLineGenerator.generate();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("버퍼크기 보다 큰 문자열일 때 버퍼크기만큼만 읽는지 테스트")
    void testGenerateWithNewLine() throws IOException {
        //given
        StringBuilder expected = new StringBuilder();
        int bufferLength = 1024;
        while(bufferLength-- > 0) {
            expected.append('a');
        }

        String longerTxt = expected.toString() + "bb";
        BufferedReader br = getBufferedReader(longerTxt);
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);

        //when
        String actual = bodyLineGenerator.generate();

        //then
        Assertions.assertThat(actual).isEqualTo(expected.toString());
    }

    @Test
    @DisplayName("default limitLength 생성자 테스트")
    void testWithDefaultLimitLength() throws IOException {
        //given
        String expected = "Hello world";
        BufferedReader br = getBufferedReader(expected);
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);

        //when
        String actual = bodyLineGenerator.generate();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("hasMoreLine() 가 BufferedReader 의 ready()값을 정상 출력하는지 테스트")
    void testHasMoreLineNormalCase() throws IOException {
        //given
        boolean expected = true;
        BufferedReader br = getBufferedReader("Hello world");
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);

        //when
        boolean actual = bodyLineGenerator.hasMoreLine();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("더이상 읽을 텍스트가 없을 때 hasMoreLine() 가 BufferedReader 의 ready()값을 정상 출력하는지 테스트")
    void testHasMoreLineWithBlankString() throws IOException {
        //given
        boolean expected = false;
        BufferedReader br = getBufferedReader("");
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);

        //when
        boolean actual = bodyLineGenerator.hasMoreLine();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("BufferedReader 가 null 일때 인스턴스 생성한 경우 예외처리 테스트")
    void testConstructorNullException() {
        //given
        BufferedReader br = null;

        Assertions.assertThatThrownBy(()->new BodyLineGenerator(br))
                .isInstanceOf(NullException.class);

    }



}