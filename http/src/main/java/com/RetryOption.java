package com;

import com.exception.InputNullParameterException;
import lombok.Builder;

import java.time.Duration;


public class RetryOption {
    private static final int DEFAULT_RETRY_COUNT = 100;
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofMillis(25);
    private final Duration waitTime;
    private int retryCount;

    @Builder
    private RetryOption(int retryCount, Duration waitTime) {
        if (waitTime == null) {
            throw new InputNullParameterException();
        }

        this.retryCount = retryCount;
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

    public boolean canRetry(int tryCount) {
        return retryCount >= tryCount;
    }
}
