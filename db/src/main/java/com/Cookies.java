package com;

import com.exception.CookieNotFoundException;
import com.exception.InputNullParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookies {
    private final Map<String,String> values = new HashMap<>();

    public void register(String key, String value) {
        if (key==null || value==null) {
            throw new InputNullParameterException();
        }
        values.put(key,value);
    }

    public String searchValue(String targetKey) {
        for(Map.Entry<String, String> cookie:values.entrySet()) {
            if (!Objects.equals(cookie.getKey(),targetKey)) {
                continue;
            }
            return cookie.getValue();
        }
        throw new CookieNotFoundException();
    }
}
