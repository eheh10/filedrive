package com.field;

import java.util.List;

public class Field {
    private final String name;
    private final List<String> values;

    public Field(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }


    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
