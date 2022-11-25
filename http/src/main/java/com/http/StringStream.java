package com.http;

import com.http.exception.InputNullParameterException;
import com.http.exception.NoMoreInputException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StringStream implements Closeable{
    private final BufferedReader br;
    private final char[] buffer = new char[8192];

    public StringStream(BufferedReader br) {
        if (br == null) {
            throw new InputNullParameterException();
        }
        this.br = br;
    }

    public static StringStream of(InputStream is ) {
        if (is == null){
            throw new InputNullParameterException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new StringStream(br);
    }

    public static StringStream empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return StringStream.of(is);
    }

    public boolean hasMoreString() {
        try {
            return br.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generate() {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        try {
            int len = br.read(buffer);
            return new String(buffer,0,len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateLine() {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
