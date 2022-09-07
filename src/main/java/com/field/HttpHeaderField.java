package com.field;

import java.util.*;

public class HttpHeaderField {
    private final String name;
    private final List<String> values;

    public HttpHeaderField(String name, List<String> values) {
        if (name == null) {
            throw new RuntimeException();
        }
        if (values == null) {
            throw new RuntimeException();
        }
        if (values.size() == 0) {
            throw new RuntimeException();
        }

        this.name = name;
        this.values = Collections.unmodifiableList(values);
    }

    public static HttpHeaderField of(String fieldLine) {
        if (fieldLine == null) {
            throw new RuntimeException();
        }

        StringTokenizer filedTokenizer = new StringTokenizer(fieldLine,":");
        if (filedTokenizer.countTokens() != 2) {
            throw new RuntimeException();
        }

        String filedName = filedTokenizer.nextToken().strip().toUpperCase();
        String filedValues = filedTokenizer.nextToken().strip();

        StringTokenizer valueTokenizer = new StringTokenizer(filedValues,",");
        List<String> values = new ArrayList<>(Math.max(10,valueTokenizer.countTokens()));

        while(valueTokenizer.hasMoreTokens()) {
            values.add(valueTokenizer.nextToken().strip());
        }

        return new HttpHeaderField(filedName, Collections.unmodifiableList(values));
    }

    public String getName() {
        return name;
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

        HttpHeaderField httpHeaderField = (HttpHeaderField) o;

        if (!Objects.equals(name, httpHeaderField.name)) return false;
        return Objects.equals(values, httpHeaderField.values);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
