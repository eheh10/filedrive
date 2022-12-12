package com.http;

import com.http.exception.InputNullParameterException;
import com.http.exception.NoMoreHttpContentException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpMessageStreams implements Closeable {
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
        ResourceStream resourceStream = ResourceStream.of(inputStream);
        return HttpMessageStreams.of(resourceStream);
    }

    public static HttpMessageStreams of(ResourceStream resourceStream) {
        HttpMessageStream httpMessageStream = HttpMessageStream.of(resourceStream);
        Queue<HttpMessageStream> values = new ArrayDeque<>();
        values.offer(httpMessageStream);

        return new HttpMessageStreams(values);
    }

    public static HttpMessageStreams empty() {
        return HttpMessageStreams.of(ResourceStream.empty());
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

    public byte[] generateByte() {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generateByte();
    }

    public String generateString() {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generateString();
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
