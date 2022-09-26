package com.limiter;

import com.exception.ExceedingHttpLengthLimitException;
import com.exception.NotPositiveNumberException;

public class HttpLengthLimiter {

    private int lengthSum = 0;
    private final int limitLength;

    public HttpLengthLimiter(int limitLength) {
        if (0 >= limitLength) {
            throw new NotPositiveNumberException();
        }

        this.limitLength = limitLength;
    }

//    public static HttpLengthLimiter Http()

    public void accumulate(int length) {
        if (length < 0) {
            throw new NotPositiveNumberException();
        }

        lengthSum += length;

        if (lengthSum > limitLength) {
            throw new ExceedingHttpLengthLimitException();
        }
    }

}
