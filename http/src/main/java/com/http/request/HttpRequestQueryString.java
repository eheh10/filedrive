package com.http.request;

import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotFoundQueryStringValueException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestQueryString {

    private final Map<String,String> values;

    private HttpRequestQueryString(Map<String, String> values) {
        if (values == null) {
            throw new InputNullParameterException();
        }

        this.values = values;
    }

    public static HttpRequestQueryString of(String queryString) {
        if (queryString == null) {
            throw new InputNullParameterException();
        }

        if (queryString.isBlank()) {
            return empty();
        }

        queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);

        Map<String,String> values = new HashMap<>();

        for(String field : queryString.split("&")) {
            String[] elements = field.split("=");

            if (elements.length != 2) {
                throw new InvalidHttpRequestInputException();
            }
            values.put(elements[0],elements[1]);
        }
        return new HttpRequestQueryString(Collections.unmodifiableMap(values));
    }

    public static HttpRequestQueryString empty() {
        return new HttpRequestQueryString(Collections.emptyMap());
    }

    public String getFieldValue(String fieldName) {
        if (!values.containsKey(fieldName)) {
            throw new NotFoundQueryStringValueException();
        }

        return values.get(fieldName);
    }

}
