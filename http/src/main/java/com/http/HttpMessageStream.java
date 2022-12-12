package com.http;

import com.http.exception.InputNullParameterException;
import com.http.releaser.NoResourceCloser;
import com.http.releaser.ResourceCloser;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpMessageStream implements Closeable {
    private final ResourceStream stream;
    private final ResourceCloser closer;

    private HttpMessageStream(ResourceStream stream, ResourceCloser closer) {
        if (stream == null || closer == null) {
            throw new InputNullParameterException();
        }

        this.stream = stream;
        this.closer = closer;
    }

    public static HttpMessageStream of(String str) {
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        return HttpMessageStream.of(is,new NoResourceCloser());
    }

    public static HttpMessageStream of(InputStream inputStream) {
        return HttpMessageStream.of(inputStream,new NoResourceCloser());
    }

    public static HttpMessageStream of(ResourceStream resourceStream) {
        return new HttpMessageStream(resourceStream,new NoResourceCloser());
    }

    public static HttpMessageStream of(InputStream inputStream, ResourceCloser closer) {
        ResourceStream resourceStream = ResourceStream.of(inputStream);
        return HttpMessageStream.of(resourceStream,closer);
    }

    public static HttpMessageStream of(ResourceStream resourceStream, ResourceCloser closer) {
        return new HttpMessageStream(resourceStream,closer);
    }

    public static HttpMessageStream empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return HttpMessageStream.of(is);
    }

    public boolean hasMoreString() {
        return stream.hasMoreString();
    }

    public byte[] generateByte() {
        return stream.generateByte();
    }

    public String generateString() {
        return stream.generateString();
    }

    public String generateLine() {
        return stream.generateLine();
    }

    @Override
    public void close() {
        closer.close();
    }
}
