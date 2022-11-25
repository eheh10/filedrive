package com.http;

import com.http.exception.InputNullParameterException;

import java.io.Closeable;

public class RetryHttpRequestStream implements Closeable {
    private final HttpMessageStream httpMessageStream;
    private final RetryOption retryOption;
    private int count = 0;

    public RetryHttpRequestStream(HttpMessageStream httpMessageStream, RetryOption retryOption) {
        if (httpMessageStream == null || retryOption == null) {
            throw new InputNullParameterException();
        }

        this.httpMessageStream = httpMessageStream;
        this.retryOption = retryOption;
    }

    public boolean hasMoreString() {
        do {
            if (httpMessageStream.hasMoreString()) {
                return true;
            }

            retryOption.waitTime();
            count++;
        }while(retryOption.canRetry(count));

        return false;
    }

    public String generate() {
        return httpMessageStream.generate();
    }

    public String generateLine() {
        return httpMessageStream.generateLine();
    }

    @Override
    public void close() {
        httpMessageStream.close();
    }
}
