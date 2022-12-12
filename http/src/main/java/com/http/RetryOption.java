package com.http;

import com.http.exception.InputNullParameterException;
import lombok.Builder;

import java.time.Duration;


public class RetryOption {
    private static final int DEFAULT_RETRY_COUNT = 100;
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofMillis(25);
    private final Duration waitTime;
    private final int maxRetryCount;

    @Builder
    private RetryOption(int maxRetryCount, Duration waitTime) {
        if (waitTime == null) {
            throw new InputNullParameterException();
        }

        this.maxRetryCount = maxRetryCount;
        this.waitTime = waitTime;
    }

    public static RetryOption standard() {
        return new RetryOption(DEFAULT_RETRY_COUNT,DEFAULT_WAIT_TIME);
    }

    public void waitTime() {
        try {
            Thread.sleep(waitTime.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }
}
