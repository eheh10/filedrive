package com.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeyParser {
    private final Map<String,String> values;

    private KeyParser(Map<String, String> values) {
        if (values==null) {
            throw new RuntimeException("values가 null");
        }

        this.values = values;
    }

    public static KeyParser of(String values) {
        return new KeyParser(parsing(values));
    }

    private static Map<String,String> parsing(String values) {
        if (values==null) {
            throw new RuntimeException("values가 null");
        }

        Map<String,String> entry = new HashMap<>();
        StringTokenizer valueTokenizer = new StringTokenizer(values,"&");

        while (valueTokenizer.hasMoreTokens()){
            StringTokenizer tokenizer = new StringTokenizer(valueTokenizer.nextToken(),"=");
            String key = tokenizer.nextToken();
            String value = URLDecoder.decode(tokenizer.nextToken(), StandardCharsets.UTF_8);

            entry.put(key,value);
        }

        return entry;
    }

    public String getValue(String key) {
        if (key==null) {
            throw new RuntimeException("key는 null일 수 없음");
        }

        if (!values.containsKey(key)) {
            throw new RuntimeException("존재하지 않는 key");
        }

        return values.get(key);
    }
}
