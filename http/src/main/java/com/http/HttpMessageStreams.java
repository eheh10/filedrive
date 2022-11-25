package com.http;

import com.http.exception.InputNullParameterException;
import com.http.exception.NoMoreHttpContentException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpMessageStreams implements Closeable{
    private final Queue<HttpMessageStream> values;

    private HttpMessageStreams(Queue<HttpMessageStream> values) {
        if (values == null) {
            throw new InputNullParameterException();
        }
        this.values = values;
    }

    public static HttpMessageStreams of(String httpMessage) {
        if (httpMessage == null){
            throw new InputNullParameterException();
        }

        InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes(StandardCharsets.UTF_8));
        return HttpMessageStreams.of(inputStream);
    }

    public static HttpMessageStreams of(InputStream inputStream) {
        StringStream stringStream = StringStream.of(inputStream);
        return HttpMessageStreams.of(stringStream);
    }

    public static HttpMessageStreams of(StringStream stringStream) {
        HttpMessageStream httpMessageStream = HttpMessageStream.of(stringStream);
        Queue<HttpMessageStream> values = new ArrayDeque<>();
        values.offer(httpMessageStream);

        return new HttpMessageStreams(values);
    }

    public static HttpMessageStreams empty() {
        return HttpMessageStreams.of(StringStream.empty());
    }

    public HttpMessageStreams sequenceOf(HttpMessageStreams streams) {
        if (streams == null) {
            throw new InputNullParameterException();
        }

        Queue<HttpMessageStream> values = new ArrayDeque<>();
        values.addAll(this.values);
        values.addAll(streams.values);

        return new HttpMessageStreams(values);
    }

    public HttpMessageStreams sequenceOf(HttpMessageStream stream) {
        if (stream == null) {
            throw new InputNullParameterException();
        }

        Queue<HttpMessageStream> values = new ArrayDeque<>();
        values.addAll(this.values);
        values.offer(stream);

        return new HttpMessageStreams(values);
    }

    public boolean hasMoreString() {
        if (values.isEmpty()) {
            return false;
        }

        if (values.size() == 1) {
            return values.peek().hasMoreString();
        }

        while(!values.peek().hasMoreString()) {
            values.poll();

            if (values.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public String generate() {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generate();
    }

    public String generateLine() {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generateLine();
    }

    @Override
    public void close() {
        while(!values.isEmpty()) {
            values.poll().close();
        }
    }
}
