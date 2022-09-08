package com.api;

import com.request.BodyLineGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class TestHttpApi implements HttpApi {

    @Override
    public String get() {
        return null;
    }

    @Override
    public String post(BodyLineGenerator bodyLineGenerator) throws IOException {
        StringBuilder responseBody = new StringBuilder();

        InputStream is2 = new FileInputStream(Paths.get("src","main","resources","response","hello.html").toString());
        BufferedInputStream bis2 = new BufferedInputStream(is2,8192);
        InputStreamReader isr2 = new InputStreamReader(bis2, StandardCharsets.UTF_8);

        char[] buffer2 = new char[1024];
        int len = -1;

        while((len=isr2.read(buffer2)) != -1) {
            responseBody.append(buffer2,0,len);
        }

        return responseBody.toString();
    }
}
