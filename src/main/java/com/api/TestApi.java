package com.api;

import com.parser.KeyParser;

public class TestApi implements TargetApi{
    private final KeyParser keyParser;

    public TestApi(KeyParser keyParser) {
        if (keyParser==null) {
            throw new RuntimeException("KeyParser가 null");
        }
        this.keyParser = keyParser;
    }

    public static TestApi of(String values) {
        KeyParser keyParser = KeyParser.of(values);
        return new TestApi(keyParser);
    }

    @Override
    public String getBody() {
        String name = keyParser.getValue("name");

        return "안녕하세요 "+name+"님";
    }
}
