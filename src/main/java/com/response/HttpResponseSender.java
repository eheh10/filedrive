package com.response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class HttpResponseSender {
    private final OutputStreamWriter osw;

    public HttpResponseSender(OutputStreamWriter osw) {
        this.osw = osw;
    }

    public static HttpResponseSender of(OutputStream os) {
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);

        return new HttpResponseSender(osw);
    }

    public void send(String responseMessage) throws IOException {
        osw.write(responseMessage);
        osw.flush();
    }

    public void close() throws IOException {
        osw.close();
    }
}
