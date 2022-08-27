package com.main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class Main2 {

    enum StatusFile {
        CODE200("200",Paths.get("src","main","resources","response","hello.html").toString()),
        CODE400("400",Paths.get("src","main","resources","response","400_error.html").toString())
        ;

        private final String statusCode;
        private final String filePath;

        StatusFile(String statusCode, String filePath) {
            this.statusCode = statusCode;
            this.filePath = filePath;
        }

        private static final Map<String,StatusFile> finder = createStatusFileFinder();
        private static Map<String, StatusFile> createStatusFileFinder() {
            Map<String,StatusFile> finder = new HashMap<>();

            for(StatusFile s:values()){
                finder.put(s.statusCode,s);
            }

            return finder;
        }

        public static StatusFile of(String statusCode) {
            return finder.get(statusCode);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        while(true) {
            Socket socket = serverSocket.accept();

            // 1. request 받기: 로직 모듈화 필요
            // request 의 구성요소를 하나로 묶기 위해 Request 클래스화 필요 - method, path, header, body, values 를 멤버변수로
            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is,8192);
            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr,8192);

            /*
            1-1. StartLine 가공
                - path 는 enum 으로 처리하여 path 에 해당되는 처리클래스와 바인딩
                - method 는 enum 으로 처리하여 메서드에 해당하는 처리클래스와 바인딩 (각 메서드 처리 클래스 필요)
                - GET 인 경우 values 가공 필요 (Values 클래스화 필요)
             */
            String startLine = br.readLine();
            if (startLine==null) {
                continue;
            }
            System.out.println("start line: "+startLine);

            /*
            1-2. Header 가공
                - Headers 클래스화 필요 - List<Filed> 를 멤버변수로
                - Field 는 String 으로 받으므로 클래스화 필요 - filed-name(대소문자구별 x), field-value 를 멤버변수로
                - HTTP Header 에 길이 제한 스펙은 없지만 자체적으로 8192로 길이 제한
                - 8192보다 긴 경우 431 Request header too large 응답
             */
            List<String> headers = new ArrayList<>();
            String line = "";

            while(!(line=br.readLine()).isEmpty()) {
                headers.add(line);
            }
            System.out.println("headers: "+headers);

            /*
            1-3. Body 가공
                - Body 는 String 으로 받으므로 클래스화 필요
                - GET,PATCH 외 메서드들은 Body 가공 필요
                - HTTP Body 에 길이 제한 스펙은 없지만 자체적으로 Header 의 Content-Length 값을 2,097,152(2MB)로 길이 제한
                - 2MB 이상이면 413 Request Entity Too Large 응답
                - values 이면 Values 클래스 가공 필요
             */
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[1024];
            int len = -1;

            // null 보다는 ready() 리턴값으로 데이터 끝 판단
            // body 의 끝에 개행문자가 없기 때문
            while(br.ready()) {
                len=br.read(buffer);
                body.append(buffer,0,len);
            }
            System.out.println("body: "+body);

            // 2. response 보내기: 로직 모듈화 필요
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os,8192);
            OutputStreamWriter bsw = new OutputStreamWriter(bos,StandardCharsets.UTF_8);

            StringBuilder responseMsg = new StringBuilder();

            String statusCode = "200";
            StatusFile statusFile = StatusFile.of(statusCode);

            responseMsg.append("HTTP/1.1 ").append(statusCode).append(" ");

            if (Objects.equals(statusCode,"200")) {
                // 상태 파일-상태 메시지를 하나의 자료구조로 만들어서 사용?
                responseMsg.append("OK");
            }

            responseMsg.append("\nContent-Type: text/html;charset=UTF-8\n\n");

            bsw.write(responseMsg.toString());

            /*
            2-1. request 처리를 통해 response 메시지 각 구성요소(StartLine,Header,Body) 얻기
                - Request 객체를 받아서 각 구성요소를 가공하는 클래스 필요
                - Status Code 와 Status Message 는 enum 으로 처리
                - body 내용이 길어지는 경우 메모리 초과될 수 있으므로 스트림 처리 필요
                - body 길이는 2,097,152(2MB)로 길이 제한
             */

            InputStream is2 = new FileInputStream(statusFile.filePath);
            BufferedInputStream bis2 = new BufferedInputStream(is2,8192);
            InputStreamReader isr2 = new InputStreamReader(bis2,StandardCharsets.UTF_8);

            char[] buffer2 = new char[1024];
            len = -1;

            while((len=isr2.read(buffer2)) != -1) {
                bsw.write(buffer2,0,len);
            }

            // 2-2. OutputStream 으로 전송
            bsw.flush();
            bsw.close();
        }
    }
}
