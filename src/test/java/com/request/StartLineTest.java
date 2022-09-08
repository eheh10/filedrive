package com.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    @DisplayName("데이터구조가 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithGetNormalData() {
        //given
        String request = "GET /test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
        } catch (Exception e) {
            Assertions.fail("fail");
        }
    }

    @Test
    @DisplayName("StartLine 에 null 이 들어왔을때 테스트")
    void testisValidDataStructureWithNULL() {
        //given
        String request = null;

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("StartLine 에 빈스트링이 들어왔을때 테스트")
    void testisValidDataStructureWithBlank() {
        //given
        String request = "";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("POST 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithPostNormalData() {
        //given
        String request = "POST /test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
        } catch (Exception e) {
            Assertions.fail("fail");
        }
    }

    @Test
    @DisplayName("PUT 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithPUTNormalData() {
        //given
        String request = "PUT /test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
        } catch (Exception e) {
            Assertions.fail("fail");
        }
    }

    @Test
    @DisplayName("DELETE 메서드로 정상적인 request 를 받았을 때 테스트")
    void testisValidDataStructureWithDELETENormalData() {
        //given
        String request = "DELETE /test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
        } catch (Exception e) {
            Assertions.fail("fail");
        }
    }

    @Test
    @DisplayName("지원하지않는 메서드로 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongMethod() {
        //given
        String request = "INVALID /test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("Path 가 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongPath() {
        //given
        String request = "GET test HTTP/1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("Version 이 잘못된 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongVersion() {
        //given
        String request = "GET /test 1.1";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("StartLine 에 불필요한 데이터가 존재하는 request 를 받았을 때 테스트")
    void testisValidDataStructureWithAdditionalData() {
        //given
        String request = "GET /test HTTP/1.1 Hi";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("StartLine 에 필요한 데이터가 부족한 request 를 받았을 때 테스트")
    void testisValidDataStructureWithWrongData() {
        //given
        String request = "GET /test";

        try {
            //when
            StartLine startLine = StartLine.parse(request);
            Assertions.fail("fail");
        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("Method 파싱 테스트")
    void testParsingMethod() {
        //given
        String expected = "GET";
        String request = "GET /test/path HTTP/1.1";

        //when
        StartLine startLine = StartLine.parse(request);
        String actual = startLine.getMethod();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Path 파싱 테스트")
    void testParsingPath() {
        //given
        String expected = "/test/path";
        String startLine = "GET /test/path HTTP/1.1";
        StartLine startLineParser = StartLine.parse(startLine);

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
        StartLine startLineParser = StartLine.parse(startLine);

        //when
        String actual = startLineParser.getVersion();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}