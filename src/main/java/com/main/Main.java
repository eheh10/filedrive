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

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            HttpRequestProcessor response = new HttpRequestProcessor();

            try (HttpStreamGenerator generator = HttpStreamGenerator.of(socket.getInputStream());
                 HttpStreamGenerator responseGenerator = response.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
                 OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {
                while (responseGenerator.hasMoreString()) {
                    bsw.write(responseGenerator.generate());
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
