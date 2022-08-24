package com.api;

import com.util.InputStreamUtil;

import java.io.IOException;
import java.nio.file.Paths;

public class NameApi implements TargetApi{
    @Override
    public String getBody() throws IOException {
        return InputStreamUtil.readFile(Paths.get("src","main","resources","test.html"));
    }
}
