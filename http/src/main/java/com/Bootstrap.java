package com;

import com.exception.EmptyRequestException;
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

public class Bootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final HttpRequestHandlers handlers = new HttpRequestHandlers();

    public void registerHandler(HttpRequestPath path, HttpRequestMethod method, HttpRequestHandler handler) {
        handlers.register(path, method, handler);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

//        Properties properties = new Properties();
//        properties.load(new FileInputStream(Path.of("config.properties").toString()));
//
//        int headersLimit = Integer.parseInt(properties.getProperty("http_request_headers_length_limit"));
//        int bodyLimit = Integer.parseInt(properties.getProperty("http_request_body_length_limit"));
        PropertyFinder finder = new PropertyFinder();

        //임피던스 불일치
        int headersLimit = Integer.parseInt(finder.find(PropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT));
        int bodyLimit = Integer.parseInt(finder.find(PropertyKey.HTTP_REQUEST_HEADERS_LENGTH_LIMIT));

        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(headersLimit);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(bodyLimit);

//        HttpLengthLimiter test = HttpLimitLength.REQUEST_BODY.createLimiter();

        while (true) {
            Socket socket = serverSocket.accept();

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os, 8192);

            HttpRequestProcessor processor = new HttpRequestProcessor();

            try (StringStream isGenerator = StringStream.of(socket.getInputStream());
                 HttpMessageStreams generator = HttpMessageStreams.of(isGenerator);
                 HttpMessageStreams responseGenerator = processor.process(generator, handlers, requestHeadersLengthLimit, requestBodyLengthLimit);
                 OutputStreamWriter bsw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {

                while (responseGenerator.hasMoreString()) {
                    String line = responseGenerator.generate();
                    LOG.debug(line);
                    bsw.write(line);
                }

                bsw.flush();
            } catch (EmptyRequestException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
