package com.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;

public class TestApi {

    public String getResponseMessage(String values){
        StringBuilder response = new StringBuilder();

        String name = "";
        StringTokenizer valueTokenizer = new StringTokenizer(values,"=");

        if (Objects.equals("name",valueTokenizer.nextToken())){
            name = URLDecoder.decode(valueTokenizer.nextToken(),StandardCharsets.UTF_8);
        }

        response.append("안녕하세요 ")
                .append(name)
                .append("님");

        return response.toString();
    }

}
