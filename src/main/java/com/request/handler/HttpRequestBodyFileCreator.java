package com.request.handler;

import com.exception.NullException;
import com.HttpStreamGenerator;
import com.header.HttpHeaders;
import com.HttpLengthLimiter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestBodyFileCreator implements HttpRequestHandler {
    @Override
    public HttpStreamGenerator handle(HttpHeaders httpHeaders, HttpStreamGenerator generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException {
        if (httpHeaders == null || generator == null) {
            throw new NullException();
        }

        Path directoryPath = Paths.get(System.getProperty("user.home"),"fileDrive");

        if (!Files.isDirectory(directoryPath)) {
            Files.createDirectory(directoryPath);
            System.out.println("directory 생성: " +directoryPath.toAbsolutePath().toString());
        }

        Path filePath = directoryPath.resolve("test.txt");
        OutputStream os = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw,8192);

        while(generator.hasMoreString()) {
            String line = generator.generate();

            requestBodyLengthLimit.accumulate(line.length());
            bw.write(line);
        }

        bw.flush();
        bw.close();

        return HttpStreamGenerator.empty();
    }
}
