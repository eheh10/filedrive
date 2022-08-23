package com.request;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class InputStreamListener {
    private final BufferedReader br;

    public InputStreamListener(BufferedReader br) {
        if (br == null){
            throw new RuntimeException("BufferedReader가 null");
        }

        this.br = br;
    }

    public static InputStreamListener of(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new InputStreamListener(br);
    }

    public String listen() throws IOException {
        StringBuilder input = new StringBuilder();
        String line = "";

        while((line= br.readLine()) != null && !Objects.equals(line,"")) {
            input.append(line).append("\n");
        }

        return input.toString();
    }


}
