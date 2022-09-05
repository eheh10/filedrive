package com.request;

import com.exception.ExceedingLengthLimitException;
import com.exception.NotPositiveNumberException;
import com.exception.NullException;

import java.io.BufferedReader;
import java.io.IOException;

public class BodyLineGenerator {
    private final BufferedReader br;
    private final char[] buffer = new char[1024];
    private int cumulativeLength = 0;
    private final int limitLength;

    public BodyLineGenerator(BufferedReader br, int limitLength) {
        if (br==null) {
            throw new NullException("RequestReader.BufferedReader is null");
        }

        if (limitLength <= 0) {
            throw new NotPositiveNumberException("RequestReader.limitLength must be positive number");
        }

        this.br = br;
        this.limitLength = limitLength;
    }

    public boolean hasMoreLine() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreLine()) {
            throw new RuntimeException();
        }

        int len = br.read(buffer);
        cumulativeLength += len;

        if (cumulativeLength > limitLength) {
            throw new ExceedingLengthLimitException("body exceeds length limit");
        }

        return new String(buffer,0,len);
    }

}
