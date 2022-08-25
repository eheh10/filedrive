package com.api;

import com.parser.KeyParser;

import java.util.Objects;

public class ApiFactory {
    public static TargetApi of(String path, String values) {
        if(Objects.equals("/",path)){
            return new FileApi("test.html");
        }

        if (Objects.equals(path,"/test")){
            String nameValue = KeyParser.of(values).getValue("name");
            return new TestApi(nameValue);
        }

        return new FileApi("error.html");
    }
}
