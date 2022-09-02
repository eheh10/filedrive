package com.request;

import com.exception.ExceedingLengthLimitException;

import java.io.IOException;

public class Body {
    private static final int LIMIT_LENGTH = 2_097_152;
    private final String value;

    private Body(String value) {
        this.value = value;
    }

    public static Body parse(RequestReader requestReader) throws IOException {
        StringBuilder body = new StringBuilder();

        while(requestReader.hasMoreContent()) {
            if (body.length() > LIMIT_LENGTH) {
                throw new ExceedingLengthLimitException("body exceeds length limit");
            }

            body.append(requestReader.read());
        }

        return new Body(body.toString());
    }
}
