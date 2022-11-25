package com.http.header;

import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {
    private static final Logger LOG = LoggerFactory.getLogger(HttpHeaders.class);
    private final Map<String, HttpHeaderField> values;

    private HttpHeaders(Map<String, HttpHeaderField> values) {
        if (values == null) {
            throw new InputNullParameterException();
        }

        this.values = Collections.unmodifiableMap(values);
    }

    /**
    * limitLength 쓰임
     * 1. 유효성 검사 - 1이상 값인지 확인
     * 2. 누적값 계산
     * 3. 예외 발생
    * */
    public static HttpHeaders parse(RetryHttpRequestStream requestStream, HttpRequestLengthLimiters lengthLimiters) {
        if (requestStream == null || lengthLimiters == null) {
            throw new InputNullParameterException();
        }

        Map<String, HttpHeaderField> fields = new HashMap<>();

        String line = "";

        while(requestStream.hasMoreString() && !((line=requestStream.generateLine()).isEmpty())) {
            lengthLimiters.accumulateHeadersLength(line.length());

            HttpHeaderField httpHeaderField = HttpHeaderField.of(line);

            fields.put(httpHeaderField.getName(), httpHeaderField);
        }

        return new HttpHeaders(Collections.unmodifiableMap(fields));
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Collections.emptyMap());
    }

    public HttpHeaderField findProperty(String propertyName) {
        if (propertyName==null) {
            throw new InputNullParameterException();
        }

        if (!values.containsKey(propertyName)) {
            throw new NotFoundHttpHeadersPropertyException();
        }

        return values.get(propertyName);
    }

    public void display() {
        for(Map.Entry<String, HttpHeaderField> entry:values.entrySet()) {
            LOG.debug(entry.getKey()+": ");
            LOG.debug(entry.getValue().toString());
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
