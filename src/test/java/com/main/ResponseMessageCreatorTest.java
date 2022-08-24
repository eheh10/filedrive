package com.main;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

class ResponseMessageCreatorTest {
    private final String responseStartLine = "HTTP/1.1 200 OK\n"+
            "Content-Type: text/html;charset=UTF-8\n\n";

    @Test
    @DisplayName("name이 영어일때 responseMessage 생성 테스트")
    void testWithEng() throws IOException {
        //given
        String name = "kim";
        String expected = responseStartLine + "안녕하세요 "+name+"님\n";
        String request = "GET /test?name="+name+" HTTP/1.1\n";
        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ResponseMessageCreator creator = new ResponseMessageCreator();

        //when
        String actual = creator.create(is);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("name이 한글일때 responseMessage 생성 테스트")
    void testWithKor() throws IOException {
        //given
        String name = "은혜";
        String expected = responseStartLine + "안녕하세요 "+name+"님\n";
        String request = "GET /test?name="+ URLEncoder.encode(name,StandardCharsets.UTF_8)+" HTTP/1.1\n";
        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ResponseMessageCreator creator = new ResponseMessageCreator();

        //when
        String actual = creator.create(is);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST로 responseMessage 생성 테스트")
    void testWithPostMethod() throws IOException {
        //given
        String name = "은혜";
        String expected = responseStartLine + "안녕하세요 "+name+"님\n";
        String request = "POST /test HTTP/1.1\n"+
                "Content-Type:text/plain\n\n"+
                "name="+ URLEncoder.encode(name,StandardCharsets.UTF_8)+"\n";

        System.out.println(request);

        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ResponseMessageCreator creator = new ResponseMessageCreator();

        //when
        String actual = creator.create(is);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("InputStream이 null일 때 런타임에러 발생 테스트")
    void testConstructWithNull() throws IOException {
        //given
        String expected = "InputStream이 null";
        ResponseMessageCreator creator = new ResponseMessageCreator();

        try{
            //when
            String result = creator.create(null);
        }catch (RuntimeException e){
            //then
            Assertions.assertThat(e.getMessage()).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("test.html 응답 테스트")
    void testTestHtml() throws IOException {
        //given
        String expected = responseStartLine +
                readFile(Paths.get("src","main","resources","test.html")) +
                "\n";
        String request = "GET / HTTP/1.1\n";
        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ResponseMessageCreator creator = new ResponseMessageCreator();

        //when
        String actual = creator.create(is);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 api로 접근시 error.html 응답 테스트")
    void testErrorHtml() throws IOException {
        //given
        String expected = responseStartLine +
                readFile(Paths.get("src","main","resources","error.html")) +
                "\n";
        String request = "GET /wrongApi HTTP/1.1\n";
        InputStream is = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
        ResponseMessageCreator creator = new ResponseMessageCreator();

        //when
        String actual = creator.create(is);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
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
}