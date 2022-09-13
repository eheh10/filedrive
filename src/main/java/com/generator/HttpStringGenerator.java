package com.generator;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpStringGenerator {
    private final BufferedReader br;
    private final char[] buffer = new char[1024];

    public HttpStringGenerator(BufferedReader br) {
        if (br == null){
            throw new RuntimeException();
        }
        this.br = br;
    }

    public static HttpStringGenerator of(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new HttpStringGenerator(br);
    }

    public boolean hasMoreText() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreText()) {
            throw new RuntimeException();
        }

        int len = br.read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreText()) {
            throw new RuntimeException();
        }
        return br.readLine();
    }
}
