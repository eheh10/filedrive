package com.http.template;

import com.http.exception.InputNullParameterException;
import com.http.exception.NoMoreFileContentException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TemplateFileStream {
    private final InputStreamReader isr;
    private final char[] buffer = new char[1024];

    private TemplateFileStream(InputStreamReader isr) {
        if (isr == null) {
            throw new InputNullParameterException();
        }
        this.isr = isr;
    }

    public static TemplateFileStream of(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);

        return new TemplateFileStream(isr);
    }

    public boolean hasMoreFileContent() {
        try {
            return isr.ready();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            isr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generate() {
        if (!hasMoreFileContent()) {
            throw new NoMoreFileContentException();
        }

        try {
            int len = isr.read(buffer);
            return new String(buffer,0,len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
