package com.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class InputStreamListenerTest {

    @Test
    @DisplayName("한줄만 입력했을때 마지막에 개행문자가 추가되는지 테스트")
    void testWithOneLine() throws IOException {
        //given
        String expected = "Hello World\n";
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        InputStreamListener inputStreamListener = InputStreamListener.of(is);

        //when
        String actual = inputStreamListener.listen();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("여러줄 입력했을때 개행문자가 유지되는지 테스트")
    void testNewline() throws IOException {
        //given
        String expected = "Hello\nWorld\n";
        InputStream is = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        InputStreamListener inputStreamListener = InputStreamListener.of(is);

        //when
        String actual = inputStreamListener.listen();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null로 인스턴스 생성시 런타임에러 발생 테스트")
    void testConstructWithNull() {
        //given
        String expected = "InputStreamReader가 null";

        try{
            //when
            InputStreamListener inputStreamListener = new InputStreamListener(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("of()에 null을 인자로 받았을때 런타임에러 발생 테스트")
    void testConstructWithNull2() {
        //given
        String expected = "BufferedReader가 null";

        try{
            //when
            InputStreamListener inputStreamListener = InputStreamListener.of(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

}