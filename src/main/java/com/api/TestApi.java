package com.api;

import com.parser.KeyParser;

public class TestApi {

    public String getResponseMessage(KeyParser nameKeyParser){
        if (nameKeyParser==null) {
            throw new RuntimeException("KeyParser가 null");
        }

        StringBuilder response = new StringBuilder();
        String name = nameKeyParser.getValue("name");

        response.append("안녕하세요 ")
                        .append(name)
                        .append("님");

        return  response.toString();
    }

}
