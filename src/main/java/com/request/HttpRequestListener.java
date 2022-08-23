package com.request;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpRequestListener {
    private final BufferedReader br;

    public HttpRequestListener(BufferedReader br) {
        this.br = br;
    }

    public static HttpRequestListener of(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        return new HttpRequestListener(br);
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
