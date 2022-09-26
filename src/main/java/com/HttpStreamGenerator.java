package com;

import com.exception.NoMoreHttpContentException;
import com.exception.NullException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpStreamGenerator implements Closeable{
    private final BufferedReader br;
    private final InputStream is;
    private final char[] buffer = new char[1024];

    private HttpStreamGenerator(InputStream is, BufferedReader br) {
        if (is == null || br == null){
            throw new NullException();
        }
        this.is = is;
        this.br = br;
    }

    public static HttpStreamGenerator empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return HttpStreamGenerator.of(is);
    }

    public static HttpStreamGenerator of(InputStream is) {
        if (is == null){
            throw new NullException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new HttpStreamGenerator(is,br);
    }

    public HttpStreamGenerator sequenceOf(HttpStreamGenerator generator) {
        if (generator == null) {
            throw new NullException();
        }

        SequenceInputStream si = new SequenceInputStream(this.is,generator.is);
        InputStreamReader isr = new InputStreamReader(si, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new HttpStreamGenerator(si,br);
    }

    public boolean hasMoreString() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        int len = br.read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }
        return br.readLine();
    }

    @Override
    public void close() throws IOException {

    }
}
