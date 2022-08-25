package com.request;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InputStreamListener {
    private final InputStreamReader isr;
    private final char[] buffer = new char[1024];

    public InputStreamListener(InputStreamReader isr) {
        if (isr == null){
            throw new RuntimeException("InputStreamReader가 null");
        }

        this.isr = isr;
    }

    public static InputStreamListener of(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        return new InputStreamListener(isr);
    }

    public String listen() throws IOException {
        StringBuilder input = new StringBuilder();
        int len = 0;

        while((len=isr.read(buffer)) != -1) {
            input.append(buffer,0,len);
        }

        return input.toString();
    }

}
