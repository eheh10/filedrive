package com;

import com.exception.InputNullParameterException;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.HttpRequestProcessor;
import com.request.handler.HttpRequestHandler;
import com.request.handler.HttpRequestHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final PreProcessorComposite preProcessorComposite;
    private final HttpRequestHandlers handlers = new HttpRequestHandlers();
    private final HttpPropertyFinder finder = new HttpPropertyFinder();

    public Bootstrap(PreProcessorComposite preProcessorComposite) {
        if (preProcessorComposite == null) {
            throw new InputNullParameterException();
        }
        this.preProcessorComposite = preProcessorComposite;
    }

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(7777);

            int headersLimit = getHttpRequestLengthLimit(HttpPropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT);
            int bodyLimit = getHttpRequestLengthLimit(HttpPropertyKey.HTTP_REQUEST_BODY_LENGTH_LIMIT);

            while (true) {
                Socket socket = serverSocket.accept();
                HttpRequestLengthLimiters requestLengthLimiters = HttpRequestLengthLimiters.from(headersLimit,bodyLimit);
                RetryOption retryOption = RetryOption.builder().retryCount(10).waitTime(Duration.ofMillis(10)).build();

                try (
                        StringStream isGenerator = StringStream.of(socket.getInputStream());
                        RetryHttpRequestStream requestStream = new RetryHttpRequestStream(HttpMessageStream.of(isGenerator),retryOption);

                        HttpRequestProcessor processor = HttpRequestProcessor.from(requestStream, handlers);
                        HttpMessageStreams responseMsg = processor.process(preProcessorComposite, requestLengthLimiters);

                        OutputStreamWriter responseSender = getResponseSender(socket.getOutputStream())
                        ) {

                    LOG.debug("<HTTP Response Message>");
                    while (responseMsg.hasMoreString()) {
                        String line = responseMsg.generate();
                        responseSender.write(line);
                        LOG.debug(line);
                    }

                    responseSender.flush();
                    LOG.debug("<--HTTP Response Message End-->");
                } finally {
                    if (socket!=null) {
                        socket.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OutputStreamWriter getResponseSender(OutputStream os) {
        BufferedOutputStream bos = new BufferedOutputStream(os, 8192);
        return new OutputStreamWriter(bos, StandardCharsets.UTF_8);
    }

    private int getHttpRequestLengthLimit(HttpPropertyKey httPropertyKey) {
        return Integer.parseInt(finder.find(httPropertyKey));
    }
}
