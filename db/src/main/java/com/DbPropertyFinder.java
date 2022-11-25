package com;

import com.exception.InputNullParameterException;
import com.exception.NotFoundPropertyException;
import com.exception.PropertyNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;


public class DbPropertyFinder {
    private final Properties value = new Properties();

    public DbPropertyFinder() {
        try {
            value.load(new FileInputStream(Path.of("db","db.properties").toString()));
        } catch (IOException e) {
            throw new NotFoundPropertyException("db.properties Not Found");
        }
    }

    private String find(DbPropertyKey key) {
        if (key == null) {
            throw new InputNullParameterException();
        }

        return value.getProperty(key.value());
    }

    public int getStorageCapacity() {
        String storageCapacity = find(DbPropertyKey.STORAGE_CAPACITY);

        if (storageCapacity == null) {
            throw new PropertyNotFoundException();
        }

        return Integer.parseInt(storageCapacity);
    }
}
