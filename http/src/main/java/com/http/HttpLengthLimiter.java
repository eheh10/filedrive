package com.http;

import com.http.exception.ExceedingHttpLengthLimitException;
import com.http.exception.MustBePositiveNumberException;

public class HttpLengthLimiter {
    private int lengthSum = 0;
    private final int limitLength;

    public HttpLengthLimiter(int limitLength) {
        if (0 >= limitLength) {
            throw new MustBePositiveNumberException();
        }

        this.limitLength = limitLength;
    }

    public void accumulate(int length) {
        if (length < 0) {
            throw new MustBePositiveNumberException();
        }

        lengthSum += length;

        if (lengthSum > limitLength) {
            throw new ExceedingHttpLengthLimitException();
        }
    }

}
