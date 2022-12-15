package com.http;

import com.http.exception.InputNullParameterException;

import java.io.Closeable;

public class RetryHttpRequestStream implements Closeable {
    private final HttpMessageStream httpMessageStream;
    private final RetryOption retryOption;

    public RetryHttpRequestStream(HttpMessageStream httpMessageStream, RetryOption retryOption) {
        if (httpMessageStream == null || retryOption == null) {
            throw new InputNullParameterException(
                    "httpMessageStream: "+httpMessageStream+"\n"+
                    "retryOption: "+retryOption+"\n"
            );
        }

        this.httpMessageStream = httpMessageStream;
        this.retryOption = retryOption;
    }

    public boolean hasMoreString() {
        int count = 0;

        do {
            if (httpMessageStream.hasMoreString()) {
                return true;
            }

            retryOption.waitTime();
            count++;
        }while(retryOption.getMaxRetryCount() >= count);

        return false;
    }

    public String generate() {
        return httpMessageStream.generateString();
    }

    public String generateLine() {
        return httpMessageStream.generateLine();
    }

    @Override
    public void close() {
        httpMessageStream.close();
    }
}
