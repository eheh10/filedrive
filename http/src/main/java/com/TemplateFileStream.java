package com;

import com.exception.InputNullParameterException;
import com.exception.NoMoreFileContentException;

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

    public boolean hasMoreFileContent() throws IOException {
        return isr.ready();
    }

    public void close() throws IOException {
        isr.close();
    }

    public String generate() throws IOException {
        if (!hasMoreFileContent()) {
            throw new NoMoreFileContentException();
        }

        int len = isr.read(buffer);
        return new String(buffer,0,len);
    }
}
