package com;

import com.exception.NoMoreHttpContentException;
import com.exception.NullException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpStreamGenerator implements Closeable{
    private final char[] buffer = new char[1024];
    private final Queue<BufferedReader> values;
    private BufferedReader currentBufferedReader = null;

    private HttpStreamGenerator(Queue<BufferedReader> values) {
        if (values == null){
            throw new NullException();
        }
        this.values = values;
    }

    public static HttpStreamGenerator empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return HttpStreamGenerator.of(is);
    }

    public static HttpStreamGenerator of(InputStream is) {
        if (is == null){
            throw new NullException();
        }

        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr,8192);

        Queue<BufferedReader> values = new ArrayDeque<>();
        values.offer(br);

        return new HttpStreamGenerator(values);
    }

    public HttpStreamGenerator sequenceOf(HttpStreamGenerator generator) {
        if (generator == null) {
            throw new NullException();
        }

        values.addAll(generator.values);

        return new HttpStreamGenerator(values);
    }

    public boolean hasMoreString() throws IOException {
        if (currentBufferedReader == null) {
            currentBufferedReader = values.poll();
        }

        while(!currentBufferedReader.ready()) {
            if (values.size() == 0) {
                return false;
            }

            currentBufferedReader.close();
            currentBufferedReader = values.poll();
        }

        return true;
    }

    public String generate() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }

        int len = currentBufferedReader.read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }
        return currentBufferedReader.readLine();
    }

    @Override
    public void close() throws IOException {
        while(values.size() != 0) {
            values.poll().close();
        }

        currentBufferedReader.close();

    }

    public void close(ResourceReleaser releaser) {
        System.out.println(releaser.release());
    }
}
