package com.request;

import com.exception.ExceedingLengthLimitException;
import com.exception.NullException;

import java.io.BufferedReader;
import java.io.IOException;

public class BodyLineGenerator {
    private final BufferedReader br;
    private final char[] buffer = new char[1024];
    private int length = 0;
    private static final int LIMIT_LENGTH = 2_097_152;

    public BodyLineGenerator(BufferedReader br) {
        if (br==null) {
            throw new NullException("RequestReader.BufferedReader is null");
        }

        this.br = br;
    }

    public boolean hasMoreLine() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreLine()) {
            throw new RuntimeException();
        }

        int len = br.read(buffer);
        length += len;

        if (len > LIMIT_LENGTH) {
            throw new ExceedingLengthLimitException("body exceeds length limit");
        }

        return new String(buffer,0,len);
    }

}
