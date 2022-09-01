package com.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class ResponseMsgCreatorTest {
    private final String responseStartLine = "HTTP/1.1 200 OK\n"+
            "Content-Type: text/html;charset=UTF-8\n\n";
    private String requestHeaders = "Host: localhost\n" +
            "Connection: keep-alive\n" +
            "Content-Length: 116\n\n";

    private static final Path FILE_PATH_200 = Path.of("src","main","resources","response","hello.html");
    private static final Path FILE_PATH_ERROR = Path.of("src","main","resources","response","error.html");

    private String createStartLine(String statusCode, String statusMsg) {
        return "HTTP/1.1 "+statusCode +" "+statusMsg+"\n"+
                "Content-Type: text/html;charset=UTF-8\n\n";
    }

    private ResponseMsgCreator createResponseMsgCreator(String str) {
        return ResponseMsgCreator.of(
                new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))
        );
    }

    private String getErrorHtml(String statusCode, String statusMsg) throws IOException {
        Map<String,String> replaceStatus = new HashMap<>();
        replaceStatus.put("statusCode",statusCode);
        replaceStatus.put("statusMsg",statusMsg);

        InputStream is = new FileInputStream(FILE_PATH_ERROR.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        int len = -1;
        char[] buffer = new char[1024];
        StringBuilder output = new StringBuilder();
        StringBuilder tmp = new StringBuilder();

        while((len=isr.read(buffer)) != -1) {
            tmp.append(buffer,0,len);

            int startIdx=tmp.indexOf("{{");
            while(startIdx > -1) {
                int endIdx = tmp.indexOf("}}");
                if (endIdx == -1) {
                    break;
                }

                String replace = tmp.substring(startIdx+2,endIdx);
                tmp.replace(startIdx,endIdx+2,replaceStatus.get(replace));

                startIdx=tmp.indexOf("{{",endIdx);
            }

            output.append(tmp);
            tmp.setLength(0);
        }

        return output.toString();
    }

    private String readFile(Path path) throws IOException {
        InputStream is = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        int len = 0;
        char[] buffer = new char[100];
        StringBuilder output = new StringBuilder();
        while((len=isr.read(buffer))!=-1){
            output.append(buffer,0,len);
        }

        return output.toString();
    }

    @Test
    @DisplayName("GET 메서드 responseMsg 200 OK 테스트")
    void testGetMethod() throws IOException {
        //given
        String expected = createStartLine("200","OK")+readFile(FILE_PATH_200);
        String request = "GET / HTTP/1.1\n"+requestHeaders;
        ResponseMsgCreator response = createResponseMsgCreator(request);

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST 메서드 responseMsg 200 OK 테스트")
    void testPOSTMethod() throws IOException {
        //given
        String expected = createStartLine("200","OK")+readFile(FILE_PATH_200);
        String request = "POST / HTTP/1.1\n"+requestHeaders;
        ResponseMsgCreator response = createResponseMsgCreator(request);

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 StartLine 구조로 request 시 responseMsg 400 error 테스트")
    void testWithWrongStartLine() throws IOException {
        //given
        String statusCode = "400";
        String statusMsg = "Bad Request";
        String expected = createStartLine(statusCode,statusMsg)+getErrorHtml(statusCode,statusMsg);
        String request = "WRONG StartLine\n"+requestHeaders;
        ResponseMsgCreator response = createResponseMsgCreator(request);

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지원하지않는 메서드로 request 시 responseMsg 400 error 테스트")
    void testWithWrongMethod() throws IOException {
        //given
        String statusCode = "400";
        String statusMsg = "Bad Request";
        String expected = createStartLine(statusCode,statusMsg)+getErrorHtml(statusCode,statusMsg);
        String request = "WRONG / HTTP/1.1\n"+requestHeaders;
        ResponseMsgCreator response = createResponseMsgCreator(request);

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("request 헤더가 길이 제한을 초과한 경우 responseMsg 431 error 테스트")
    void testHeaderLimitLength() throws IOException {
        //given
        String statusCode = "431";
        String statusMsg = "Request header too large";
        String expected = createStartLine(statusCode,statusMsg)+getErrorHtml(statusCode,statusMsg);
        StringBuilder request = new StringBuilder().append("GET / HTTP/1.1\n").append("TEST:");
        int limit = 750;
        while(limit-- > 0) {
            request.append("12345678910");
        }
        ResponseMsgCreator response = createResponseMsgCreator(request.toString());

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("request 바디가 길이 제한을 초과한 경우 responseMsg 413 error 테스트")
    void testBodyLimitLength() throws IOException {
        //given
        String statusCode = "413";
        String statusMsg = "Request Entity Too Large";
        String expected = createStartLine(statusCode,statusMsg)+getErrorHtml(statusCode,statusMsg);
        StringBuilder request = new StringBuilder().append("POST / HTTP/1.1\n\n");
        int limit = 5000;
        while(limit-- > 0) {
            request.append("12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910" +
                    "12345678910123456789101234567891012345678910");
        }
        ResponseMsgCreator response = createResponseMsgCreator(request.toString());

        //when
        String actual = response.create();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}