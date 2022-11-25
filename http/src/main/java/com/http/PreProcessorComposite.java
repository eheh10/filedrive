package com.http;

import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreProcessorComposite implements PreProcessor {
    private final List<PreProcessor> values;

    private PreProcessorComposite(List<PreProcessor> values) {
        if (values == null) {
            throw new InputNullParameterException();
        }
        this.values = Collections.unmodifiableList(values);
    }

    public static PreProcessorComposite empty() {
        List<PreProcessor> values = new ArrayList<>();
        return new PreProcessorComposite(values);
    }

    public static PreProcessorComposite of(PreProcessor preProcessor) {
        List<PreProcessor> values = new ArrayList<>();
        values.add(preProcessor);
        return new PreProcessorComposite(values);
    }

    public PreProcessorComposite sequenceOf(PreProcessor preProcessor) {
        if (preProcessor == null) {
            throw new InputNullParameterException();
        }

        List<PreProcessor> values = new ArrayList<>();
        values.addAll(values);
        values.add(preProcessor);

        return new PreProcessorComposite(values);
    }

    @Override
    public void process(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders) {
        if (httpRequestPath == null || httpHeaders == null) {
            throw new InputNullParameterException();
        }

        for(PreProcessor preProcessor : values) {
            preProcessor.process(httpRequestPath, httpHeaders);
        }
    }
}
