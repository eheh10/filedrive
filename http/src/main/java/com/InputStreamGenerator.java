package com;

import com.exception.InputNullParameterException;
import com.exception.NoMoreInputException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InputStreamGenerator implements Closeable{
    private final BufferedReader br;
    private final char[] buffer = new char[8192];

    public InputStreamGenerator(BufferedReader br) {
        if (br == null) {
            throw new InputNullParameterException();
        }
        this.br = br;
    }

    public static InputStreamGenerator of(InputStream is ) {
        if (is == null){
            throw new InputNullParameterException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new InputStreamGenerator(br);
    }

    public boolean hasMoreString() throws IOException {
        return br.ready();
    }

    public String generate() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        int len = br.read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        return br.readLine();
    }

    @Override
    public void close() throws IOException {
        br.close();
    }
}
