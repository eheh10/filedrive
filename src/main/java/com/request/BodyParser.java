package com.request;

import java.io.IOException;

public class BodyParser {
    private static final int LIMIT_LENGTH = 2_097_152;

    public String parse(RequestReader requestReader) throws IOException {
        StringBuilder body = new StringBuilder();
        int len = 0;

        while(requestReader.hasMoreContent()) {
            if (body.length() > LIMIT_LENGTH) {
                throw new RuntimeException();
            }

            body.append(requestReader.read());
        }

        return body.toString();
    }
}
