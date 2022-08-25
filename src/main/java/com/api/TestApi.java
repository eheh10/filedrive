package com.api;

public class TestApi implements TargetApi{
    private final String name;

    public TestApi(String name) {
        if (name==null) {
            throw new RuntimeException("name이 null");
        }
        this.name = name;
    }


    @Override
    public String getBody() {
        return "안녕하세요 "+name+"님";
    }
}
