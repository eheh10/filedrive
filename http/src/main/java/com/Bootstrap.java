package com;

import com.exception.FaviconException;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestHandler;
import com.request.handler.HttpRequestHandlers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Bootstrap {
    private final HttpRequestHandlers handlers = new HttpRequestHandlers();

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(8192);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(2_097_152);

//        HttpLengthLimiter test = HttpLimitLength.REQUEST_BODY.createLimiter();

        while (true) {
            Socket socket = serverSocket.accept();

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            HttpRequestProcessor processor = new HttpRequestProcessor();

            try (HttpStreamGenerator generator = HttpStreamGenerator.of(socket.getInputStream());
                 HttpStreamGenerator responseGenerator = processor.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
                 OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {

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
