package com;

import com.exception.NoMoreHttpContentException;
import com.exception.InputNullParameterException;
import com.releaser.ResourceCloser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

public class HttpStreamGenerator implements Closeable{
    private final char[] buffer = new char[8192];
    private final Queue<BufferedReader> values;
    private final Queue<ResourceCloser> releasers = new ArrayDeque<>();

    private HttpStreamGenerator(Queue<BufferedReader> values) {
        if (values == null){
            throw new InputNullParameterException();
        }
        this.values = values;
    }

    public static HttpStreamGenerator empty() {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        return HttpStreamGenerator.of(is);
    }

    public static HttpStreamGenerator of(InputStream is) {
        if (is == null){
            throw new InputNullParameterException();
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
            throw new InputNullParameterException();
        }

//        Queue<BufferedReader> values = new ArrayDeque<>();
//
//        for(BufferedReader br : this.values) {
//            InputStream is = new ByteArrayInputStream(br.);
//            BufferedInputStream bis = new BufferedInputStream(is,8192);
//            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
//            BufferedReader newBr = new BufferedReader(isr,8192);
//
//            values.add(newBr);
//        }
//
//        for(BufferedReader br : generator.values) {
//            InputStream is = new ByteArrayInputStream(br.);
//            BufferedInputStream bis = new BufferedInputStream(is,8192);
//            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
//            BufferedReader newBr = new BufferedReader(isr,8192);
//
//            values.add(newBr);
//        }

        values.addAll(generator.values);

        Queue<ResourceCloser> releasers = new ArrayDeque<>();
        for(ResourceCloser releaser:this.releasers) {
            releasers.add(releaser);
        }
        for(ResourceCloser releaser:generator.releasers) {
            releasers.add(releaser);
        }

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

        while(!values.peek().ready()) {
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

        int len = values.peek().read(buffer);
        return new String(buffer,0,len);
    }

    public String generateLine() throws IOException {
        if (!hasMoreString()) {
            throw new NoMoreHttpContentException();
        }
        return values.peek().readLine();
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
