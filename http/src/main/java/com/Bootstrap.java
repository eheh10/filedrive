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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final PreProcessor preProcessor;
    private final HttpRequestHandlers handlers = new HttpRequestHandlers();
    private final HttpPropertyFinder finder = new HttpPropertyFinder();

    public Bootstrap(PreProcessor preProcessor) {
        if (preProcessor == null) {
            throw new InputNullParameterException();
        }
        this.preProcessor = preProcessor;
    }

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(7777);

            int headersLimit = finder.httpRequestHeadersLengthLimit();
            int bodyLimit = finder.httpRequestBodyLengthLimit();
            int threadPoolCount = finder.httpRequestProcessThreadPoolCount();

            Executor executor = Executors.newFixedThreadPool(threadPoolCount);

            while (true) {
                Socket socket = serverSocket.accept();
                HttpRequestLengthLimiters requestLengthLimiters = HttpRequestLengthLimiters.from(headersLimit,bodyLimit);
                RetryOption retryOption = RetryOption.builder().retryCount(10).waitTime(Duration.ofMillis(10)).build();

                executor.execute(() -> {
                    try (
                            StringStream isGenerator = StringStream.of(socket.getInputStream());
                            RetryHttpRequestStream requestStream = new RetryHttpRequestStream(HttpMessageStream.of(isGenerator),retryOption);

                            HttpRequestProcessor processor = HttpRequestProcessor.from(requestStream,handlers,preProcessor,requestLengthLimiters);
                            HttpMessageStreams responseMsg = processor.process();

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OutputStreamWriter getResponseSender(OutputStream os) {
        BufferedOutputStream bos = new BufferedOutputStream(os, 8192);
        return new OutputStreamWriter(bos, StandardCharsets.UTF_8);
    }

}
