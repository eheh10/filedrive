package com.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestReader {
    private final BufferedReader br;
    private final char[] buffer = new char[1024];

    public RequestReader(BufferedReader br) {
        if (br==null) {
            throw new RuntimeException("RequestReader.BufferedReader is null");
        }

        this.br = br;
    }

    public boolean hasMoreContent() throws IOException {
        return br.ready();
    }

    public StringBuilder read() throws IOException {
        int len = br.read(buffer);

        if (len == -1) {
            return new StringBuilder("");
        }

        return new StringBuilder().append(buffer,0,len);
    }

}
