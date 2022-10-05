package com.header;

import com.HttpLengthLimiter;
import com.HttpStreamGenerator;
import com.exception.NotFoundHttpHeadersPropertyException;
import com.exception.NullException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {

    private final Map<String, HttpHeaderField> values;

    private HttpHeaders(Map<String, HttpHeaderField> values) {
        if (values == null) {
            throw new NullException();
        }

        this.values = Collections.unmodifiableMap(values);
    }

    /**
    * limitLength 쓰임
     * 1. 유효성 검사 - 1이상 값인지 확인
     * 2. 누적값 계산
     * 3. 예외 발생
    * */
    public static HttpHeaders parse(HttpStreamGenerator generator, HttpLengthLimiter limitLength) throws IOException {
        if (generator == null || limitLength == null) {
            throw new NullException();
        }

        Map<String, HttpHeaderField> fields = new HashMap<>();

        String line = "";

        while(!(line=generator.generateLine()).isEmpty()) {
            limitLength.accumulate(line.length());

            HttpHeaderField httpHeaderField = HttpHeaderField.of(line);

            fields.put(httpHeaderField.getName(), httpHeaderField);
        }

        return new HttpHeaders(Collections.unmodifiableMap(fields));
    }

    public static HttpHeaders parse(HttpStreamGenerator generator) throws IOException {
        HttpLengthLimiter lengthLimit = new HttpLengthLimiter(8192);
        return parse(generator, lengthLimit);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Collections.emptyMap());
    }

    public HttpHeaderField findProperty(String propertyName) {
        if (propertyName==null) {
            throw new NullException();
        }

        if (!values.containsKey(propertyName)) {
            throw new NotFoundHttpHeadersPropertyException();
        }

        return values.get(propertyName);
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
        return values.hashCode();
    }
}
