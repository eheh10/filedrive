package com.http.template;

import com.http.exception.NotFoundTemplateException;
import com.http.exception.InputNullParameterException;

import java.util.*;

public class TemplateNodes {
    private final Map<String,String> values = new HashMap<>();
    private final Queue<Integer> templateLength = new PriorityQueue<>(Collections.reverseOrder());

    public void register(String templateTxt, String replaceTxt) {
        if (templateTxt == null || replaceTxt == null) {
            throw new InputNullParameterException();
        }

        values.put(templateTxt, replaceTxt);
        templateLength.add(templateTxt.length());
    }

    public String replace(String templateTxt) {
        if (templateTxt == null) {
            throw new InputNullParameterException();
        }

        if (!values.containsKey(templateTxt)) {
            throw new NotFoundTemplateException();
        }

        return values.get(templateTxt);
    }

    public String replaceWithDefault(String templateTxt, String defaultTxt) {
        if (templateTxt == null) {
            throw new InputNullParameterException();
        }

        if (!values.containsKey(templateTxt)) {
            return defaultTxt;
        }

        return values.get(templateTxt);
    }

    public int getTemplateMaxLength() {
        return templateLength.peek();
    }
}
