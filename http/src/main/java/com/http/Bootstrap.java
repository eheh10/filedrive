package com.http;

import com.http.exception.EmptyRequestException;
import com.http.exception.InputNullParameterException;
import com.http.request.HttpRequestProcessor;
import com.http.request.handler.HttpRequestHandlers;
import com.http.response.HttpResponseStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final PreProcessor preProcessor;
    private final HttpRequestHandlers handlers;
    private final HttpPropertyFinder finder = new HttpPropertyFinder();

    public Bootstrap(PreProcessor preProcessor, HttpRequestHandlers handlers) {
        if (preProcessor == null || handlers == null) {
            throw new InputNullParameterException(
                    "preProcessor: "+preProcessor+"\n" +
                            "handlers: "+handlers
            );
        }
        this.preProcessor = preProcessor;
        this.handlers = handlers;
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
                RetryOption retryOption = getRetryOption(50,50);

                executor.execute(() -> {
                    try (
                            RetryHttpRequestStream requestStream = createRetryHttpRequestStream(socket.getInputStream(), retryOption);

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

    private RetryHttpRequestStream createRetryHttpRequestStream(InputStream is, RetryOption retryOption) {
        return new RetryHttpRequestStream(HttpMessageStream.of(is),retryOption);
    }

    private RetryOption getRetryOption(int macRetryCount, int milliSeconds) {
        return RetryOption.builder().maxRetryCount(macRetryCount).waitTime(Duration.ofMillis(milliSeconds)).build();
    }

}
