package com.main;

import com.api.TestApi;
import com.parser.KeyParser;
import com.request.InputStreamListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringTokenizer;

public class ResponseMessageCreator {

    public String create(InputStreamListener inputstreamListener) throws IOException {
        if (inputstreamListener==null) {
            throw new RuntimeException("InputstreamListener이 null");
        }

        String requestInput = inputstreamListener.listen();
        System.out.println(requestInput);

        if (requestInput.length() == 0) {
            return "";
        }

        String startLine = requestInput.split("\n")[0];
        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        String method = tokenizer.nextToken();
        String target = tokenizer.nextToken();

        StringBuilder responseMessage = new StringBuilder();

        responseMessage.append("HTTP/1.1 200 OK\n"+
                "Content-Type: text/html;charset=UTF-8\n"+
                "\n");

        String path = target.split("\\?")[0];
        if(Objects.equals("/",path)){
            responseMessage.append(readFile(Paths.get("src","main","resources","test.html"))).append("\n");
            return responseMessage.toString();
        }

        if (Objects.equals(path,"/test")){
            TestApi testApi = new TestApi();
            String values = "";

            if (Objects.equals(method,"GET")) {
                values = target.split("\\?")[1];
            }else if (Objects.equals(method,"POST")) {
                values = requestInput.split("\n\n")[1].strip();
            }

            KeyParser nameKeyParser = KeyParser.of(values);
            String testResponse = testApi.getResponseMessage(nameKeyParser);

            responseMessage.append(testResponse).append("\n");
        }else {
            responseMessage.append(readFile(Paths.get("src","main","resources","error.html"))).append("\n");
        }

        return responseMessage.toString();
    }

    private String readFile(Path path) throws IOException {
        if (path==null) {
            return "";
        }

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
