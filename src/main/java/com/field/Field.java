package com.field;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (!Objects.equals(name, field.name)) return false;
        return Objects.equals(values, field.values);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
