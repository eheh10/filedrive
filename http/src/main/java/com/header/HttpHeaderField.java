package com.header;

import com.exception.InvalidHttpRequestInputException;
import com.exception.NullException;

import java.util.*;

public class HttpHeaderField {
    private final String name;
    private final List<String> values;

    public HttpHeaderField(String name, List<String> values) {
        if (name == null || values == null) {
            throw new NullException();
        }
        if (values.size() == 0) {
            throw new RuntimeException();
        }

        this.name = name;
        this.values = Collections.unmodifiableList(values);
    }

    public static HttpHeaderField of(String fieldLine) {
        if (fieldLine == null) {
            throw new NullException();
        }

        int delimiterIdx = fieldLine.indexOf(":");
        if (delimiterIdx == -1) {
            throw new InvalidHttpRequestInputException("Invalid HttpHeadersField");
        }

        String filedName = fieldLine.substring(0,delimiterIdx);
        String filedValues = fieldLine.substring(delimiterIdx+1);

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
