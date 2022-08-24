package com.api;

import java.util.Objects;

public class ApiFactory {
    public static TargetApi of(String path, String values) {
        if(Objects.equals("/",path)){
            return new NameApi();
        }

        if (Objects.equals(path,"/test")){
            return TestApi.of(values);
        }

        return new ErrorApi();
    }
}
