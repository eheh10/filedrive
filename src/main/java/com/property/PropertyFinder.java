package com.property;

import com.db.exception.NotFoundPropertyException;
import com.http.exception.InputNullParameterException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyFinder {
    private static final String PROPERTY_FILE_PATH = Path.of("filedrive.properties").toString();
    private static final PropertyFinder INSTANCE = new PropertyFinder(createProperties());
    private final Properties value;

    private PropertyFinder(Properties value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public static PropertyFinder getInstance() {
        return INSTANCE;
    }

    private static Properties createProperties() {
        try {
            Properties value = new Properties();
            value.load(new FileInputStream(PROPERTY_FILE_PATH));

            return value;
        } catch (IOException e) {
            throw new NotFoundPropertyException("filedrive.properties Not Found");
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
