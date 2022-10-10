package com;

import com.exception.FaviconException;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestHandler;
import com.request.handler.HttpRequestHandlers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public class Bootstrap {
    private final HttpRequestHandlers handlers = new HttpRequestHandlers();

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        Properties properties = new Properties();
        properties.load(new FileInputStream(Path.of("config.properties").toString()));

        int headersLimit = Integer.parseInt(properties.getProperty("http_request_headers_length_limit"));
        int bodyLimit = Integer.parseInt(properties.getProperty("http_request_body_length_limit"));

        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(headersLimit);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(bodyLimit);

//        HttpLengthLimiter test = HttpLimitLength.REQUEST_BODY.createLimiter();

        while (true) {
            Socket socket = serverSocket.accept();

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            HttpRequestProcessor processor = new HttpRequestProcessor();

            try (InputStreamGenerator isGenerator = InputStreamGenerator.of(socket.getInputStream());
                 HttpStreamGenerator generator = HttpStreamGenerator.of(isGenerator);
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
