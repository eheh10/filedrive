package com.api;

import java.util.HashMap;
import java.util.Map;

public class ApiGenerator {
    private static Map<String,TargetApi> values = new HashMap<>();

    public ApiGenerator(Map<String, TargetApi> values) {
        if (values==null) {
            throw new RuntimeException("values가 null");
        }

        this.values = values;
    }

    public TargetApi to(String api) {
        if (api==null) {
            throw new RuntimeException("api가 null");
        }

        if (!values.containsKey(api)) {
            return new FileApi("error.html");
        }

        return values.get(api);
    }
}
