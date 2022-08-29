package com.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StartLineParserTest {

    @Test
    @DisplayName("데이터구조가 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithGetNormalData() {
        //given
        boolean expected = true;
        String startLine = "GET /test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("StartLine 에 null 이 들어왔을때 테스트")
    void testisValidDataStructureWithNULL() {
        //given
        boolean expected = false;
        String startLine = null;

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("StartLine 에 빈스트링이 들어왔을때 테스트")
    void testisValidDataStructureWithBlank() {
        //given
        boolean expected = false;
        String startLine = "";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithPostNormalData() {
        //given
        boolean expected = true;
        String startLine = "POST /test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("PUT 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithPUTNormalData() {
        //given
        boolean expected = true;
        String startLine = "PUT /test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("DELETE 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithDELETENormalData() {
        //given
        boolean expected = true;
        String startLine = "DELETE /test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지원하지않는 메서드로 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongMethod() {
        //given
        boolean expected = false;
        String startLine = "INVALID /test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Path 가 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongPath() {
        //given
        boolean expected = false;
        String startLine = "GET test HTTP/1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Version 이 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongVersion() {
        //given
        boolean expected = false;
        String startLine = "GET /test 1.1";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("StartLine 에 불필요한 데이터가 존재하는 request 를 받았을 때 테스트")
    void testisValidDataStructureWithAdditionalData() {
        //given
        boolean expected = false;
        String startLine = "GET /test HTTP/1.1 Hi";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("StartLine 에 필요한 데이터가 부족한 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongData() {
        //given
        boolean expected = false;
        String startLine = "GET /test";

        //when
        boolean actual = StartLineParser.isValidDataStructure(startLine);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Path 파싱 테스트")
    void testParsingPath() {
        //given
        String expected = "/test/path";
        String startLine = "GET /test/path HTTP/1.1";
        StartLineParser startLineParser = StartLineParser.of(startLine);

        //when
        String actual = startLineParser.getPath();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Version 파싱 테스트")
    void testParsingVersion() {
        //given
        String expected = "HTTP/1.1";
        String startLine = "GET /test HTTP/1.1";
        StartLineParser startLineParser = StartLineParser.of(startLine);

        //when
        String actual = startLineParser.getVersion();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}