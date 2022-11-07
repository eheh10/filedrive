package com;

import com.exception.InputNullParameterException;
import com.exception.NotFoundResourceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class PropertyFinder {
    private final Properties value = new Properties();

    public PropertyFinder() {
        try {
            value.load(new FileInputStream(Path.of("config.properties").toFile()));
        } catch (IOException e) {
            throw new NotFoundResourceException("Not Found config.properties");
        }
    }

    public String find(PropertyKey key) {
        if (key == null) {
            throw new InputNullParameterException();
        }
        return value.getProperty(key.value());
    }
}
