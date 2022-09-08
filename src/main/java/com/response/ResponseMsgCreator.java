package com.response;

import com.request.BodyLineGenerator;
import com.request.HttpHeaders;
import com.request.StartLine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class ResponseMsgCreator {

    private String statusCode = "200";
    private String statusMsg = "OK";
    private final BufferedReader br;

    private ResponseMsgCreator(BufferedReader br) {
        if (br == null) {
            throw new RuntimeException();
        }
        this.br = br;
    }

    public static ResponseMsgCreator of(InputStream is) {
        if (is == null) {
            return null;
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new ResponseMsgCreator(br);
    }

    public String create() throws IOException {

        // 1. request 받기: 로직 모듈화 필요
        // request 의 구성요소를 하나로 묶기 위해 Request 클래스화 필요 - method, path, header, body, values 를 멤버변수로

        /*
            1-1. StartLine 가공
                - path 는 enum 으로 처리하여 path 에 해당되는 처리클래스와 바인딩
                - method 는 enum 으로 처리하여 메서드에 해당하는 처리클래스와 바인딩 (각 메서드 처리 클래스 필요)
                - GET 인 경우 values 가공 필요 (Values 클래스화 필요)
        */
        if (!br.ready()) {
            return null;
        }

        String startLine = br.readLine();
        System.out.println(startLine);

        StartLine startLineParser = null;
        try {
            startLineParser = StartLine.parse(startLine);
        }catch (RuntimeException e) {
            statusCode = "400";
            statusMsg = "Bad Request";
        }


        /*
            1-2. Header 가공
                - Headers 클래스화 필요 - List<Filed> 를 멤버변수로
                - Field 는 String 으로 받으므로 클래스화 필요 - filed-name(대소문자구별 x), field-value 를 멤버변수로
                - HTTP Header 에 길이 제한 스펙은 없지만 자체적으로 8192로 길이 제한
                - 8192보다 긴 경우 431 Request header too large 응답
        */
        HttpHeaders httpHeaders = null;
        try {
            httpHeaders = HttpHeaders.parse(br,8192);
            httpHeaders.display();
        }catch (RuntimeException e) {
            statusCode = "431";
            statusMsg = "Request header too large";
        }

        /*
            1-3. Body 가공
                - Body 는 String 으로 받으므로 클래스화 필요
                - GET,PATCH 외 메서드들은 Body 가공 필요
                - HTTP Body 에 길이 제한 스펙은 없지만 자체적으로 Header 의 Content-Length 값을 2,097,152(2MB)로 길이 제한
                - 2MB 이상이면 413 Request Entity Too Large 응답
                - values 이면 Values 클래스 가공 필요
       */
        BodyLineGenerator bodyLineGenerator = new BodyLineGenerator(br);
        try {
            while(bodyLineGenerator.hasMoreLine()) {
                System.out.print(bodyLineGenerator.generate());
            }
        }catch (RuntimeException e) {
            statusCode = "413";
            statusMsg = "Request Entity Too Large";
        }

        // 2. response 보내기: 로직 모듈화 필요
        StringBuilder responseMsg = new StringBuilder();

        Map<String,String> replaceTxt = new HashMap<>();
        replaceTxt.put("statusCode",statusCode);
        replaceTxt.put("statusMsg",statusMsg);

        responseMsg.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMsg).append("\n")
                .append("Content-Type: text/html;charset=UTF-8\n\n");

        /*
            2-1. request 처리를 통해 response 메시지 각 구성요소(StartLine,Header,Body) 얻기
                - Request 객체를 받아서 각 구성요소를 가공하는 클래스 필요
                - Status Code 와 Status Message 는 enum 으로 처리
                - body 내용이 길어지는 경우 메모리 초과될 수 있으므로 스트림 처리 필요
                - body 길이는 2,097,152(2MB)로 길이 제한
        */

        if (Objects.equals(statusCode,"200")) {
            ResponseBodyGenerator bodyGenerator = new ResponseBodyGenerator(startLineParser,httpHeaders,bodyLineGenerator);
            String responseBody = bodyGenerator.generate();
            responseMsg.append(responseBody);

            return responseMsg.toString();
        }

        InputStream is2 = new FileInputStream(Paths.get("src","main","resources","response","error.html").toString());
        BufferedInputStream bis2 = new BufferedInputStream(is2,8192);
        InputStreamReader isr2 = new InputStreamReader(bis2,StandardCharsets.UTF_8);

        char[] buffer2 = new char[1024];
        int len = -1;

        StringBuilder tmp = new StringBuilder();
        boolean replaceMode = false;

        while((len=isr2.read(buffer2)) != -1) {
            tmp.append(buffer2,0,len);

            int startIdx=tmp.indexOf("{");
            while(startIdx > -1) {
                if (startIdx == tmp.length()-1) {
                    replaceMode = true;
                    break;
                }

                if (tmp.charAt(startIdx+1) != '{') {
                    replaceMode = false;
                    break;
                }

                int endIdx = tmp.indexOf("}}", startIdx+1);
                if (endIdx == -1) {
                    replaceMode = true;
                    break;
                }

                String replace = tmp.substring(startIdx+2,endIdx);
                tmp.replace(startIdx,endIdx+2,replaceStatus.get(replace));

                startIdx=tmp.indexOf("{",endIdx);
                replaceMode = false;
            }

            if (replaceMode) {
                continue;
            }

            responseMsg.append(tmp);
            tmp.setLength(0);
        }

        return responseMsg.toString();
    }

}
