package com;

import com.exception.InputNullParameterException;
import com.exception.NoMoreHttpContentException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpMessageStreams implements Closeable{
    private final Queue<HttpMessageStream> values;

    private HttpMessageStreams(Queue<HttpMessageStream> values) {
        this.values = values;
    }

    public static HttpMessageStreams of(StringStream stream) {
        if (stream == null){
            throw new InputNullParameterException();
        }

        HttpMessageStream httpMessageStream = HttpMessageStream.of(stream);
        Queue<HttpMessageStream> values = new ArrayDeque<>();
        values.offer(httpMessageStream);

        return new HttpMessageStreams(values);
    }

    public static HttpMessageStreams empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        StringStream stream = StringStream.of(is);
        return HttpMessageStreams.of(stream);
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

    public boolean hasMoreString() throws IOException {
        if (values.isEmpty()) {
            return false;
        }

        while(!values.peek().hasMoreString()) {
            if (values.size() == 1) {
                return false;
            }

            values.poll();
        }

        return true;
    }

    public String generate() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generate();
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        return values.peek().generateLine();
    }

    @Override
    public void close() throws IOException {
        while(!values.isEmpty()) {
            values.poll().close();
        }
    }
}
