package com.property;

import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundResourceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class PropertyFinder {
    private final Properties value = new Properties();

    public PropertyFinder() {
        try {
            value.load(new FileInputStream(Path.of("filedrive.properties").toFile()));
        } catch (IOException e) {
            throw new NotFoundResourceException("Not Found filedrive.properties");
        }
    }

    private String find(PropertyKey key) {
        if (key == null) {
            throw new InputNullParameterException();
        }
        return value.getProperty(key.value());
    }

    public int fileDownloadCountLimit() {
        return Integer.parseInt(find(PropertyKey.FILE_DOWNLOAD_COUNT_LIMIT));
    }
}
