package com.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

class DbPropertyFinderTest {
    private static final Properties PROPERTIES = new Properties();
    private static final DbPropertyFinder PROPERTY_FINDER = DbPropertyFinder.getInstance();

    @BeforeAll
    static void loadProperties() throws IOException {
        PROPERTIES.load(new FileInputStream(Path.of("C:","dev","intellij","filedrive","db","db.properties").toFile()));
    }

    @Test
    @DisplayName("DB_URL property 테스트")
    void testOfDbConnectionUrl() {
        //given
        String key = DbPropertyKey.DB_URL.value();
        String expected = PROPERTIES.getProperty(key);

        //when
        String actual = PROPERTY_FINDER.getDbConnectionUrl();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("DB_USER property 테스트")
    void testOfDbUser() {
        //given
        String key = DbPropertyKey.DB_USER.value();
        String expected = PROPERTIES.getProperty(key);

        //when
        String actual = PROPERTY_FINDER.getDbUser();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("DB_PASSWORD property 테스트")
    void testOfDbPwd() {
        //given
        String key = DbPropertyKey.DB_PASSWORD.value();
        String expected = PROPERTIES.getProperty(key);

        //when
        String actual = PROPERTY_FINDER.getDbPwd();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("STORAGE_CAPACITY property 테스트")
    void testOfStorageCapacity() {
        //given
        String key = DbPropertyKey.STORAGE_CAPACITY.value();
        int expected = Integer.parseInt(PROPERTIES.getProperty(key));

        //when
        int actual = PROPERTY_FINDER.getStorageCapacity();

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}