package com;

import com.exception.InputNullParameterException;
import com.releaser.NoResourceCloser;
import com.releaser.ResourceCloser;

import java.io.Closeable;
import java.io.IOException;

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

    public static HttpMessageStream of(StringStream stream) {
        return new HttpMessageStream(stream,new NoResourceCloser());
    }

    public static HttpMessageStream of(StringStream stream, ResourceCloser closer) {
        return new HttpMessageStream(stream,closer);
    }

    public boolean hasMoreString() throws IOException {
        return stream.hasMoreString();
    }

    public String generate() throws IOException {
        return stream.generate();
    }

    public String generateLine() throws IOException {
        return stream.generateLine();
    }

    @Override
    public void close() throws IOException {
        closer.close();
    }
}
