package com.db;

import com.db.exception.InputNullParameterException;
import com.db.exception.NotFoundPropertyException;
import com.db.exception.PropertyNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;


public class DbPropertyFinder {
    private static final String PROPERTY_FILE_PATH = Path.of("db","db.properties").toString();
    private static final DbPropertyFinder INSTANCE = new DbPropertyFinder(createProperties());
    private final Properties value;

    private DbPropertyFinder(Properties value) {
        if (value == null) {
            throw new InputNullParameterException();
        }

        this.value = value;
    }

    public static DbPropertyFinder getInstance() {
        return INSTANCE;
    }

    private static Properties createProperties() {
        try {
            Properties value = new Properties();
            value.load(new FileInputStream(PROPERTY_FILE_PATH));

            return value;
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

    public String getDbConnectionUrl() {
        return find(DbPropertyKey.DB_URL);
    }

    public String getDbUser() {
        return find(DbPropertyKey.DB_USER);
    }

    public String getDbPwd() {
        return find(DbPropertyKey.DB_PASSWORD);
    }

    public int getStorageCapacity() {
        String storageCapacity = find(DbPropertyKey.STORAGE_CAPACITY);

        if (storageCapacity == null) {
            throw new PropertyNotFoundException();
        }

        return Integer.parseInt(storageCapacity);
    }
}
