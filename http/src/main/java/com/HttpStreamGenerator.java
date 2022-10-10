package com;

import com.exception.NoMoreHttpContentException;
import com.exception.InputNullParameterException;
import com.releaser.ResourceCloser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpStreamGenerator implements Closeable{
    private final Queue<InputStreamGenerator> values;
    private final Queue<ResourceCloser> releasers = new ArrayDeque<>();

    private HttpStreamGenerator(Queue<InputStreamGenerator> values) {
        if (values == null){
            throw new InputNullParameterException();
        }
        this.values = values;
    }

    public static HttpStreamGenerator empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        InputStreamGenerator isGenerator = InputStreamGenerator.of(is);
        return HttpStreamGenerator.of(isGenerator);
    }

    public static HttpStreamGenerator of(InputStreamGenerator isGenerator) {
        if (isGenerator == null){
            throw new InputNullParameterException();
        }

        Queue<InputStreamGenerator> values = new ArrayDeque<>();
        values.offer(isGenerator);

        return new HttpStreamGenerator(values);
    }

    public HttpStreamGenerator sequenceOf(HttpStreamGenerator generator) {
        if (generator == null) {
            throw new InputNullParameterException();
        }

        Queue<InputStreamGenerator> values = new ArrayDeque<>();
        values.addAll(this.values);
        values.addAll(generator.values);

        Queue<ResourceCloser> releasers = new ArrayDeque<>();
        releasers.addAll(this.releasers);
        releasers.addAll(generator.releasers);

        return new HttpStreamGenerator(values);
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
