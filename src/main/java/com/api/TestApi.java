package com.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;

public class TestApi {

    public String getResponseMessage(String values){
        if (values == null){
            throw new RuntimeException("values가 null");
        }

        StringBuilder response = new StringBuilder();
        StringTokenizer valueTokenizer = new StringTokenizer(values,"&");

        while (valueTokenizer.hasMoreTokens()){
            StringTokenizer tokenizer = new StringTokenizer(valueTokenizer.nextToken(),"=");
            String key = tokenizer.nextToken();

            if (Objects.equals("name",key)){
                String name = URLDecoder.decode(tokenizer.nextToken(), StandardCharsets.UTF_8);

                response.append("안녕하세요 ")
                        .append(name)
                        .append("님");

                return response.toString();
            }
        }

        throw new RuntimeException("name key가 없음");
    }

}
