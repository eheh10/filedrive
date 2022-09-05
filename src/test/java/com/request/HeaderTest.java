package com.request;

import com.exception.NotPositiveNumberException;
import com.exception.NullException;
import com.exception.StatusCode431Exception;
import com.field.Field;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class HeaderTest {
    private int limitLength = 8192;
    private String fields = "Host: localhost\n" +
            "Connection: keep-alive\n" +
            "Content-Length: 116\n\n";

    private Header revertHeader(String fields) {
        Map<String,Field> values = new HashMap<>();

        StringTokenizer tokenizer = new StringTokenizer(fields,"\n");
        while(tokenizer.hasMoreTokens()) {

            StringTokenizer filedTokenizer = new StringTokenizer(tokenizer.nextToken(),":");
            String filedName = filedTokenizer.nextToken().strip().toUpperCase();
            String filedValues = filedTokenizer.nextToken().strip();

            StringTokenizer valueTokenizer = new StringTokenizer(filedValues, ",");
            List<String> fieldValues = new ArrayList<>(Math.max(10, valueTokenizer.countTokens()));

            while (valueTokenizer.hasMoreTokens()) {
                fieldValues.add(valueTokenizer.nextToken().strip());
            }

            Field field = new Field(filedName, fieldValues);

            values.put(filedName, field);
        }

        return new Header(Collections.unmodifiableMap(values));
    }

    private BufferedReader getBufferedReader(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);
        return br;
    }

    @Test
    @DisplayName("value가 한개인 필드 정상 파싱 테스트")
    void testParsingHeaderWithNormalCase1() throws IOException {
        //given
        String field = "Host: localhost\n\n";
        Header expected = revertHeader(field);
        BufferedReader br = getBufferedReader(field);

        //when
        Header actual = Header.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("value가 여러개인 필드 정상 파싱 테스트")
    void testParsingHeaderWithNormalCase2() throws IOException {
        //given
        Header expected = revertHeader(fields);
        BufferedReader br = getBufferedReader(fields);

        //when
        Header actual = Header.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("field value 가 여러개일때 정상 파싱 테스트")
    void testParsingHeaderWithValues() throws IOException {
        //given
        String field = "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n\n";
        Header expected = revertHeader(field);
        BufferedReader br = getBufferedReader(field);

        //when
        Header actual = Header.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("default limitLength 정상 파싱 테스트")
    void testParsingWithDefaultLimitLength() throws IOException {
        //given
        Header expected = revertHeader(fields);
        BufferedReader br = getBufferedReader(fields);

        //when
        Header actual = Header.parse(br);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("BufferedReader 가 null 일때 런타임에러 발생 테스트")
    void testParsingWithNull() throws IOException {
        //given
        BufferedReader br = null;

        Assertions.assertThatThrownBy(()->Header.parse(br,limitLength))
                .isInstanceOf(NullException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,-1})
    @DisplayName("limitLength 가 0이하일때 런타임에러 발생 테스트")
    void testParsingWithWrongLimitLength(int limitLength) throws IOException {
        //given
        BufferedReader br = getBufferedReader(fields);

        Assertions.assertThatThrownBy(()->Header.parse(br,limitLength))
                .isInstanceOf(NotPositiveNumberException.class);
    }

    @Test
    @DisplayName("limitLength 를 초과했을때 런타임에러 발생 테스트")
    void testParsingWithExceedingLimitLength() throws IOException {
        //given
        BufferedReader br = getBufferedReader(fields);
        limitLength = 10;

        Assertions.assertThatThrownBy(()->Header.parse(br,limitLength))
                .isInstanceOf(StatusCode431Exception.class);
    }

}