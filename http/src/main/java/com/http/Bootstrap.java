package com.http;

import com.http.exception.EmptyRequestException;
import com.http.exception.InputNullParameterException;
import com.http.request.HttpRequestMethod;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestProcessor;
import com.http.request.handler.HttpRequestHandler;
import com.http.request.handler.HttpRequestHandlers;
import com.http.response.HttpResponseStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
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
                RetryOption retryOption = RetryOption.builder().maxRetryCount(50).waitTime(Duration.ofMillis(50)).build();

                executor.execute(() -> {
                    try (
                            ResourceStream isGenerator = ResourceStream.of(socket.getInputStream());
                            RetryHttpRequestStream requestStream = new RetryHttpRequestStream(HttpMessageStream.of(isGenerator),retryOption);

                            HttpRequestProcessor processor = HttpRequestProcessor.from(requestStream,handlers,preProcessor,requestLengthLimiters);
                            HttpResponseStream responseMsg = processor.process();

                            BufferedOutputStream responseSender = new BufferedOutputStream(socket.getOutputStream())
                    ) {
                        LOG.debug("<HTTP Response Message>");
                        while (responseMsg.hasMoreMessage()) {
                            byte[] msg = responseMsg.generate();
                            responseSender.write(msg);
                            LOG.debug(new String(msg,StandardCharsets.UTF_8));
                        }

                        responseSender.flush();
                        LOG.debug("<--HTTP Response Message End-->");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EmptyRequestException e) {
                        return;
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
