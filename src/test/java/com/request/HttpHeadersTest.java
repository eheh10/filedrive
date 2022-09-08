package com.request;

import com.exception.ExceedingLengthLimitException;
import com.exception.NotPositiveNumberException;
import com.exception.NullException;
import com.field.HttpHeaderField;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.*;

class HttpHeadersTest {
    private final Class<HttpHeaders> httpHeadersClass = HttpHeaders.class;
    private final Constructor<HttpHeaders> constructor = httpHeadersClass.getDeclaredConstructor(Map.class);
    private int limitLength = 8192;
    private static final String fields = "Host: localhost\n" +
            "Connection: keep-alive\n" +
            "Content-Length: 116\n\n";

    HttpHeadersTest() throws NoSuchMethodException {
        constructor.setAccessible(true);
    }

    private HttpHeaders revertHeader(String fields) {
        Map<String, HttpHeaderField> values = new HashMap<>();

        try {
            StringTokenizer tokenizer = new StringTokenizer(fields, "\n");
            while (tokenizer.hasMoreTokens()) {

                StringTokenizer filedTokenizer = new StringTokenizer(tokenizer.nextToken(), ":");
                String filedName = filedTokenizer.nextToken().strip().toUpperCase();
                String filedValues = filedTokenizer.nextToken().strip();

                StringTokenizer valueTokenizer = new StringTokenizer(filedValues, ",");
                List<String> fieldValues = new ArrayList<>(Math.max(10, valueTokenizer.countTokens()));

                while (valueTokenizer.hasMoreTokens()) {
                    fieldValues.add(valueTokenizer.nextToken().strip());
                }

                HttpHeaderField httpHeaderField = new HttpHeaderField(filedName, fieldValues);

                values.put(filedName, httpHeaderField);
            }

            return constructor.newInstance(Collections.unmodifiableMap(values));
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedReader getBufferedReader(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);
        return br;
    }

    @ParameterizedTest
    @ValueSource(strings = {"Host: localhost\n\n",fields})
    @DisplayName("필드 정상 파싱 테스트")
    void testParsingHeaderWithNormalCase1(String field) throws IOException, NoSuchMethodException {
        //given
        HttpHeaders expected = revertHeader(field);
        BufferedReader br = getBufferedReader(field);

        //when
        HttpHeaders actual = HttpHeaders.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("field value 가 여러개일때 정상 파싱 테스트")
    void testParsingHeaderWithValues() throws IOException, NoSuchMethodException {
        //given
        String field = "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n\n";
        HttpHeaders expected = revertHeader(field);
        BufferedReader br = getBufferedReader(field);

        //when
        HttpHeaders actual = HttpHeaders.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("default limitLength 정상 파싱 테스트")
    void testParsingWithDefaultLimitLength() throws IOException, NoSuchMethodException {
        //given
        HttpHeaders expected = revertHeader(fields);
        BufferedReader br = getBufferedReader(fields);

        //when
        HttpHeaders actual = HttpHeaders.parse(br);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("BufferedReader 가 null 일때 런타임에러 발생 테스트")
    void testParsingWithNull() throws IOException {
        //given
        BufferedReader br = null;

        Assertions.assertThatThrownBy(()-> HttpHeaders.parse(br,limitLength))
                .isInstanceOf(NullException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,-1})
    @DisplayName("limitLength 가 0이하일때 런타임에러 발생 테스트")
    void testParsingWithWrongLimitLength(int limitLength) throws IOException {
        //given
        BufferedReader br = getBufferedReader(fields);

        Assertions.assertThatThrownBy(()-> HttpHeaders.parse(br,limitLength))
                .isInstanceOf(NotPositiveNumberException.class);
    }

    @Test
    @DisplayName("limitLength 를 초과했을때 런타임에러 발생 테스트")
    void testParsingWithExceedingLimitLength() throws IOException {
        //given
        BufferedReader br = getBufferedReader(fields);
        limitLength = 10;

        Assertions.assertThatThrownBy(()-> HttpHeaders.parse(br,limitLength))
                .isInstanceOf(ExceedingLengthLimitException.class);
    }

}