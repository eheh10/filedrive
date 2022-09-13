package com.api;

import com.generator.InputStreamTextGenerator;
import com.request.HttpHeaders;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestBodyFileCreator implements HttpRequestHandler{
    @Override
    public String handle(HttpHeaders httpHeaders, InputStreamTextGenerator generator) throws IOException {
        Path directoryPath = Paths.get(System.getProperty("user.home"),"fileDrive");

        if (!Files.isDirectory(directoryPath)) {
            Files.createDirectory(directoryPath);
            System.out.println("directory 생성: " +directoryPath.toAbsolutePath().toString());
        }

        Path filePath = directoryPath.resolve("test.txt");
        OutputStream os = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);


        while(generator.hasMoreText()) {
            osw.write(generator.generate());
        }

        osw.flush();
        osw.close();

        return "";
    }
}