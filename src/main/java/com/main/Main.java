package com.main;

import com.HttpLengthLimiter;
import com.HttpStreamGenerator;
import com.exception.FaviconException;
import com.releaser.FileResourceReleaser;
import com.releaser.ResourceReleaser;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestBodyFileCreator;
import com.request.handler.HttpRequestHandlers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        HttpRequestHandlers handlers = new HttpRequestHandlers();
        handlers.register(HttpRequestPath.of("/test"), HttpRequestMethod.POST, new HttpRequestBodyFileCreator());

        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(8192);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(2_097_152);

        while (true) {
            Socket socket = serverSocket.accept();

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            HttpRequestProcessor processor = new HttpRequestProcessor();

            try (HttpStreamGenerator generator = HttpStreamGenerator.of(socket.getInputStream());
                 HttpStreamGenerator responseGenerator = processor.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
                 OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {

                ResourceReleaser releaser = new FileResourceReleaser(Path.of("src","main","resources","response","errorBody.html").toFile());
                responseGenerator.registerReleaser(releaser);

                while (responseGenerator.hasMoreString()) {
                    String line = responseGenerator.generate();
                    System.out.println(line);
                    bsw.write(line);
                }
                bsw.flush();
            } catch (FaviconException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
