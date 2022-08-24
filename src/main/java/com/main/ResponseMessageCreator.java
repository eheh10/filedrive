package com.main;

import com.api.ApiFactory;
import com.api.TargetApi;

import java.io.IOException;
import java.util.Objects;
import java.util.StringTokenizer;

public class ResponseMessageCreator {

    public String createResponseTo(String requestInput) throws IOException {
        if (requestInput==null) {
            throw new RuntimeException("requestInput이 null");
        }

        if (requestInput.length() == 0) {
            return "";
        }

        String startLine = requestInput.split("\n")[0];
        StringTokenizer tokenizer = new StringTokenizer(startLine," ");
        String method = tokenizer.nextToken();
        String target = tokenizer.nextToken();
        String path = target.split("\\?")[0];

        String values = parseValues(method,target,requestInput);

        TargetApi targetApi = ApiFactory.of(path,values);
        String body = targetApi.getBody();

        StringBuilder responseMessage = new StringBuilder();

        responseMessage.append("HTTP/1.1 200 OK\n"+
                "Content-Type: text/html;charset=UTF-8\n"+
                "\n")
                .append(body)
                .append("\n");

        return responseMessage.toString();
    }

    private String parseValues(String method, String target, String requestInput) {
        String values = "";

        if (Objects.equals(method,"GET")) {
            String[] elements = target.split("\\?");

            if(elements.length < 2){
                return "";
            }

            return elements[1];
        }

        if (Objects.equals(method,"POST")) {
            return requestInput.split("\n\n")[1].strip();
        }

        return values;
    }
}
