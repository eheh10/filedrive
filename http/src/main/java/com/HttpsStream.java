package com;

import com.exception.NoMoreHttpContentException;
import com.exception.InputNullParameterException;
import com.releaser.ResourceCloser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpsStream implements Closeable{
    private final Queue<StringStream> values;
    private final Queue<ResourceCloser> releasers = new ArrayDeque<>();

    private HttpsStream(Queue<StringStream> values) {
        if (values == null){
            throw new InputNullParameterException();
        }
        this.values = values;
    }

    public static HttpsStream empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        StringStream isGenerator = StringStream.of(is);
        return HttpsStream.of(isGenerator);
    }

    public static HttpsStream of(StringStream isGenerator) {
        if (isGenerator == null){
            throw new InputNullParameterException();
        }

        Queue<StringStream> values = new ArrayDeque<>();
        values.offer(isGenerator);

        return new HttpsStream(values);
    }

    public HttpsStream sequenceOf(HttpsStream generator) {
        if (generator == null) {
            throw new InputNullParameterException();
        }

        Queue<StringStream> values = new ArrayDeque<>();
        values.addAll(this.values);
        values.addAll(generator.values);

        Queue<ResourceCloser> releasers = new ArrayDeque<>();
        releasers.addAll(this.releasers);
        releasers.addAll(generator.releasers);

        return new HttpsStream(values);
    }

    public void registerReleaser(ResourceCloser releaser) {
        if (releaser == null) {
            throw new InputNullParameterException();
        }

        releasers.add(releaser);
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

        while(!releasers.isEmpty()) {
            releasers.poll().close();
        }
    }
}
