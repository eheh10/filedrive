package com.request;

import com.exception.ExceedingLengthLimitException;
import com.exception.NotPositiveNumberException;
import com.exception.NullException;
import com.field.HttpHeaderField;
import com.generator.InputStreamTextGenerator;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {

    private final Map<String, HttpHeaderField> values;

    private HttpHeaders(Map<String, HttpHeaderField> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    /**
    * limitLength 쓰임
     * 1. 유효성 검사 - 1이상 값인지 확인
     * 2. 누적값 계산
     * 3. 예외 발생
    * */
    public static HttpHeaders parse(InputStreamTextGenerator generator, int limitLength) throws IOException {
        if (generator == null) {
            throw new NullException("HeaderParser.parse().TextGenerator is null");
        }
        // int 의 사용성을 제한 필요
        if (limitLength < 1) {
            throw new NotPositiveNumberException("HeaderParser.parse().limitLength must be positive number");
        }

        Map<String, HttpHeaderField> fields = new HashMap<>();

        String line = "";
        int headerLength = 0;

        while(!(line=generator.generateLine()).isEmpty()) {
            headerLength += line.length();

            if (headerLength > limitLength) {
                throw new ExceedingLengthLimitException("Headers 가 제한길이를 초과");
            }

            HttpHeaderField httpHeaderField = HttpHeaderField.of(line);

            fields.put(httpHeaderField.getName(), httpHeaderField);
        }

        return new HttpHeaders(Collections.unmodifiableMap(fields));
    }

    public static HttpHeaders parse(InputStreamTextGenerator generator) throws IOException {
        return parse(generator, 8192);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Collections.emptyMap());
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
