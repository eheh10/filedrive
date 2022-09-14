package com.request;

import com.exception.ExceedingLengthLimitException;
import com.exception.NotPositiveNumberException;

public class StringLengthLimit {
    private int lengthSum = 0;
    private final int limitLength;

    public StringLengthLimit(int limitLength) {
        if (0 >= limitLength) {
            throw new NotPositiveNumberException();
        }

        this.limitLength = limitLength;
    }

    public void accumulate(String str) {
        if (str == null) {
            return;
        }

        lengthSum += str.length();

        if (lengthSum > limitLength) {
            throw new ExceedingLengthLimitException();
        }
    }

}
