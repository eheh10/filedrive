package com;

import com.exception.InputNullParameterException;
import com.exception.NoMoreInputException;

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

    public boolean hasMoreString() throws IOException {
        int retry = 100;
        int tryCount = 0;

        do {
            if (br.ready()) {
                return true;
            }
            tryCount++;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }while(tryCount < retry);

        return false;
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
