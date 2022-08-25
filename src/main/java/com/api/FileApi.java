package com.api;

import com.util.InputStreamUtil;

import java.io.IOException;
import java.nio.file.Paths;

public class FileApi implements TargetApi{
    private final String fileName;

    public FileApi(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getBody() throws IOException {
        return InputStreamUtil.readFile(Paths.get("src","main","resources",fileName));
    }
}
