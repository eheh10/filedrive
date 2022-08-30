package com.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

class HeaderParserTest {
    private String fields = "Host: localhost\n" +
            "Connection: keep-alive\n" +
            "Content-Length: 116\n\n";

    private BufferedReader getBufferedReader(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);
        return br;
    }

    @Test
    @DisplayName("value가 여러개인 필드 정상 파싱 테스트")
    void testParsingHeaderWithNormalCase() throws IOException {
        //given
        Map<String,List<String>> expected = Map.of(
                "HOST",List.of("localhost"),
                "CONNECTION",List.of("keep-alive"),
                "CONTENT-LENGTH",List.of("116")
        );
        BufferedReader br = getBufferedReader(fields);
        int limitLength = 8192;

        //when
        Map<String,List<String>> actual = HeaderParser.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("field value 가 여러개일때 정상 파싱 테스트")
    void testParsingHeaderWithValues() throws IOException {
        //given
        Map<String,List<String>> expected = Map.of(
                "ACCEPT-LANGUAGE",List.of("ko-KR","ko;q=0.9","en-US;q=0.8","en;q=0.7")
        );
        String field = "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n\n";
        BufferedReader br = getBufferedReader(field);
        int limitLength = 8192;

        //when
        Map<String,List<String>> actual = HeaderParser.parse(br,limitLength);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("BufferedReader 가 null 일때 런타임에러 발생 테스트")
    void testParsingWithNull() throws IOException {
        //given
        String expected = "HeaderParser.parse().BufferedReader is null";
        BufferedReader br = null;
        int limitLength = 8192;

        try{
            //when
            Map<String,List<String>> actual = HeaderParser.parse(br,limitLength);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("limitLength 가 0일때 런타임에러 발생 테스트")
    void testParsingWithZeroLimitLength() throws IOException {
        //given
        String expected = "HeaderParser.parse().limitLength must be greater than 0";
        BufferedReader br = getBufferedReader(fields);
        int limitLength = 0;

        try{
            //when
            Map<String,List<String>> actual = HeaderParser.parse(br,limitLength);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("limitLength 가 0이하일때 런타임에러 발생 테스트")
    void testParsingWithWrongLimitLength() throws IOException {
        //given
        String expected = "HeaderParser.parse().limitLength must be greater than 0";
        BufferedReader br = getBufferedReader(fields);
        int limitLength = -1;

        try{
            //when
            Map<String,List<String>> actual = HeaderParser.parse(br,limitLength);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("default limitLength 정상 파싱 테스트")
    void testParsingWithDefaultLimitLength() throws IOException {
        //given
        Map<String,List<String>> expected = Map.of(
                "HOST",List.of("localhost"),
                "CONNECTION",List.of("keep-alive"),
                "CONTENT-LENGTH",List.of("116")
        );
        BufferedReader br = getBufferedReader(fields);

        //when
        Map<String,List<String>> actual = HeaderParser.parse(br);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}