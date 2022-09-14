package com.generator;

import com.exception.NoMoreStringException;
import com.exception.NullException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpStringGenerator {
    private final BufferedReader br;
    private final char[] buffer = new char[1024];

    public HttpStringGenerator(BufferedReader br) {
        if (br == null){
            throw new NullException();
        }
        this.br = br;
    }

    public static HttpStringGenerator empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return HttpStringGenerator.of(is);
    }

    public static HttpStringGenerator of(InputStream is) {
        if (is == null){
            throw new NullException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new HttpStringGenerator(br);
    }

    public boolean hasMoreString() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreStringException();
        }

        int len = br.read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreStringException();
        }
        return br.readLine();
    }
}
