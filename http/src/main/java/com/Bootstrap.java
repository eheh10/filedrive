package com;

import com.exception.EmptyRequestException;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestHandler;
import com.request.handler.HttpRequestHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final HttpRequestHandlers handlers = new HttpRequestHandlers();
    private final PropertyFinder finder = new PropertyFinder();

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(7777);

            int headersLimit = Integer.parseInt(finder.find(PropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT));
            int bodyLimit = Integer.parseInt(finder.find(PropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT));

            Socket socket = null;

            while (true) {
                try {
                    HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(headersLimit);
                    HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(bodyLimit);

                    socket = serverSocket.accept();

                    OutputStream os = socket.getOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

                    HttpRequestProcessor processor = new HttpRequestProcessor();

                    try (StringStream isGenerator = StringStream.of(socket.getInputStream());
                         HttpMessageStream generator = HttpMessageStream.of(isGenerator);
                         HttpMessageStreams responseMsg = processor.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
                         OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {

                        while (responseMsg.hasMoreString()) {
                            String line = responseMsg.generate();
                            LOG.debug(line);
                            bsw.write(line);
                        }
                        bsw.flush();
                    } catch (EmptyRequestException e) {
                        continue;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (socket == null) {
                        continue;
                    }
                    socket.close();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
