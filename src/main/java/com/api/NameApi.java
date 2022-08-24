package com.api;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NameApi implements TargetApi{
    @Override
    public String getBody() throws IOException {
        return readFile(Paths.get("src","main","resources","test.html"));
    }

    private String readFile(Path path) throws IOException {
        if (path==null) {
            throw new RuntimeException("존재하지 않는 경로");
        }

        InputStream is = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        int len = 0;
        char[] buffer = new char[100];
        StringBuilder output = new StringBuilder();
        while((len=isr.read(buffer))!=-1){
            output.append(buffer,0,len);
        }

        return output.toString();
    }
}
