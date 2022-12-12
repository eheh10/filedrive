package com.http.response;

import com.http.HttpMessageStream;
import com.http.HttpMessageStreams;
import com.http.exception.InputNullParameterException;

import java.io.Closeable;
import java.io.IOException;

public class HttpResponseStream implements Closeable {
    private final HttpMessageStreams messageStreams;

    private HttpResponseStream(HttpMessageStreams messageStreams) {
        if (messageStreams == null) {
            throw new InputNullParameterException();
        }

        this.messageStreams = messageStreams;
    }

    public static HttpResponseStream from(HttpResponseStatus status, HttpMessageStream headers, HttpMessageStream body) {
        String startLine = "HTTP/1.1 "+status.code()+" "+status.message();

        return new HttpResponseStream(
                HttpMessageStreams.of(startLine)
                        .sequenceOf(HttpMessageStream.of("\n"))
                        .sequenceOf(headers)
                        .sequenceOf(HttpMessageStream.of("\n\n"))
                        .sequenceOf(body)
        );
    }

    public boolean hasMoreMessage() {
        return messageStreams.hasMoreString();
    }

    public byte[] generate() {
        return messageStreams.generateByte();
    }

    @Override
    public void close() throws IOException {
        messageStreams.close();
    }
}
