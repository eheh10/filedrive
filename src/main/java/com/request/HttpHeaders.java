package com.request;

import com.exception.NotPositiveNumberException;
import com.exception.NullException;
import com.exception.StatusCode431Exception;
import com.field.HttpHeaderField;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class HttpHeaders {

    private final Map<String, HttpHeaderField> values;

    private HttpHeaders(Map<String, HttpHeaderField> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    public static HttpHeaders parse(BufferedReader br, int limitLength) throws IOException {
        if (br == null) {
            throw new NullException("HeaderParser.parse().BufferedReader is null");
        }
        if (limitLength < 1) {
            throw new NotPositiveNumberException("HeaderParser.parse().limitLength must be positive number");
        }

        Map<String, HttpHeaderField> fields = new HashMap<>();

        String line = "";
        int headerLength = 0;

        while(!(line=br.readLine()).isEmpty()) {
            headerLength += line.length();

            if (headerLength > limitLength) {
                throw new StatusCode431Exception();
            }

            HttpHeaderField httpHeaderField = HttpHeaderField.of(line);

            fields.put(httpHeaderField.getName(), httpHeaderField);
        }

        return new HttpHeaders(Collections.unmodifiableMap(fields));
    }

    public static HttpHeaders parse(BufferedReader br) throws IOException {
        return parse(br, 8192);
    }

    public void display() {
        for(Map.Entry<String, HttpHeaderField> entry:values.entrySet()) {
            System.out.print(entry.getKey()+": ");
            System.out.println(entry.getValue().toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpHeaders httpHeaders = (HttpHeaders) o;

        return Objects.equals(values, httpHeaders.values);
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }
}
