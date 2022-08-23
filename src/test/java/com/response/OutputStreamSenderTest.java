package com.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class OutputStreamSenderTest {

    @Test
    @DisplayName("문자열 정상 출력 테스트")
    void testOutput() throws IOException {
        //given
        String expected = "Hello world\n";
        OutputStream os = new ByteArrayOutputStream(100);
        OutputStreamSender outputStreamSender = OutputStreamSender.of(os);

        //when
        outputStreamSender.send(expected);
        String actual = os.toString();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null일때 인스턴스 생성시 런타임에러 발생 테스트")
    void testConstructWithNull() {
        //given
        String expected = "OutputStreamWriter null";

        try{
            //when
            OutputStreamSender outputStreamSender = new OutputStreamSender(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("of()에 null을 인자로 받았을때 런타임에러 발생 테스트")
    void testConstructWithNull2() {
        //given
        String expected = "OutputStreamWriter null";

        try{
            //when
            OutputStreamSender outputStreamSender = OutputStreamSender.of(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

}