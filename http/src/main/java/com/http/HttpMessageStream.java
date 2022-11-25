package com.http;

import com.http.exception.InputNullParameterException;
import com.http.releaser.NoResourceCloser;
import com.http.releaser.ResourceCloser;

import java.io.Closeable;
import java.io.InputStream;

public class HttpMessageStream implements Closeable {
    private final StringStream stream;
    private final ResourceCloser closer;

    private HttpMessageStream(StringStream stream, ResourceCloser closer) {
        if (stream == null || closer == null) {
            throw new InputNullParameterException();
        }

        this.stream = stream;
        this.closer = closer;
    }

    public static HttpMessageStream of(InputStream inputStream) {
        return HttpMessageStream.of(inputStream,new NoResourceCloser());
    }

    public static HttpMessageStream of(StringStream stringStream) {
        return new HttpMessageStream(stringStream,new NoResourceCloser());
    }

    public static HttpMessageStream of(InputStream inputStream, ResourceCloser closer) {
        StringStream stringStream = StringStream.of(inputStream);
        return HttpMessageStream.of(stringStream,closer);
    }

    public static HttpMessageStream of(StringStream stringStream, ResourceCloser closer) {
        return new HttpMessageStream(stringStream,closer);
    }

    public boolean hasMoreString() {
        return stream.hasMoreString();
    }

    public String generate() {
        return stream.generate();
    }

    public String generateLine() {
        return stream.generateLine();
    }

    @Override
    public void close() {
        closer.close();
    }
}
