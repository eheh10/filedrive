package com.main;

import com.HttpStreamGenerator;
import com.exception.FaviconException;
import com.HttpLengthLimiter;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestBodyFileCreator;
import com.request.handler.HttpRequestHandlers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        HttpRequestHandlers handlers = new HttpRequestHandlers();
        handlers.register(HttpRequestPath.of("/test"), HttpRequestMethod.POST, new HttpRequestBodyFileCreator());

        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(8192);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(2_097_152);

        while (true) {
            Socket socket = serverSocket.accept();

            HttpStreamGenerator generator = HttpStreamGenerator.of(socket.getInputStream());
            HttpRequestProcessor response = new HttpRequestProcessor();

            HttpStreamGenerator responseGenerator = null;
            try {
                responseGenerator = response.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
            } catch (FaviconException e) {
                continue;
            }

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            try (OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);) {
                while (responseGenerator.hasMoreString()) {
//                    String str = responseGenerator.generate();
//                    System.out.println(str);
                    bsw.write(responseGenerator.generate());
                }
                System.out.println("end");
                bsw.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
//            bsw.close();
        }
    }
}
