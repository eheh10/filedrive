package com;

import com.http.HttpMessageStream;
import com.http.ResourceStream;
import com.http.exception.InputNullParameterException;
import com.http.closer.ResourceCloser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

class HttpMessageStreamTest {
    private static final ByteArrayOutputStream OUTPUT_STREAM = new ByteArrayOutputStream();
    private static final PrintStream PRINT_STREAM = System.out;
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(OUTPUT_STREAM));
    }

    @AfterEach
    void tearDown() {
        System.setOut(PRINT_STREAM);
    }

    class TestResourceCloser implements ResourceCloser {
        @Override
        public boolean close() {
            System.out.println("TestResourceCloser.close() is called");
            return true;
        }
    }

    @Test
    @DisplayName("Closer 등록시 close 테스트")
    void testCloser() throws IOException {
        //given
        String expected = "TestResourceCloser.close() is called";
        String str = "Hello";
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        ResourceStream resourceStream = ResourceStream.of(is);

        ResourceCloser closer = new TestResourceCloser();
        HttpMessageStream stream = HttpMessageStream.of(resourceStream,closer);

        //when
        stream.close();
        String actual = OUTPUT_STREAM.toString().trim();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null 로 인스턴스 생성시 에러 발생 테스트")
    void testConstructWithNull() {
        Assertions.assertThatThrownBy(()-> HttpMessageStream.of((InputStream) null))
                .isInstanceOf(InputNullParameterException.class);
        Assertions.assertThatThrownBy(()-> HttpMessageStream.of((InputStream) null,null))
                .isInstanceOf(InputNullParameterException.class);
    }

}