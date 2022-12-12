package com.http;

import com.http.exception.InputNullParameterException;
import com.http.exception.NoMoreInputException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ResourceStream implements Closeable{
    private final InputStream is;
    private final BufferedReader br;
    private final byte[] byteBuffer = new byte[8192];
    private final char[] charBuffer = new char[8192];

    public ResourceStream(InputStream is, BufferedReader br) {
        if (is == null || br == null) {
            throw new InputNullParameterException(
                    "InputStream: "+ is +"\n" +
                    "BufferedReader:"+br
            );
        }
        this.is = is;
        this.br = br;
    }

    public static ResourceStream of(InputStream is ) {
        if (is == null){
            throw new InputNullParameterException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new ResourceStream(is,br);
    }

    public static ResourceStream empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return ResourceStream.of(is);
    }

    public boolean hasMoreString() {
        try {
            return br.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateByte() {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        try {
            int len = is.read(byteBuffer);
            return Arrays.copyOf(byteBuffer,len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateString() {
        if (!hasMoreString()) {
            throw new NoMoreInputException();
        }

        try {
            int len = br.read(charBuffer);
            return new String(charBuffer,0,len);
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
