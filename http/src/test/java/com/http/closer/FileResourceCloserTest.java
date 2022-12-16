package com.http.closer;

import com.http.exception.InputNullParameterException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

class FileResourceCloserTest {
    private static final File NEW_FILE = new File(Paths.get("src","test","resources","closerTest.txt").toString());

    @BeforeEach
    private void createTestFile() {
        try {
            NEW_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    private void removeTestFile() {
        if (NEW_FILE.exists()) {
            NEW_FILE.delete();
        }
    }

    @DisplayName("파일 삭제 테스트")
    @Test
    void testFileRemove() {
        // given
        FileResourceCloser closer = new FileResourceCloser(NEW_FILE);

        // when
        closer.close();
        boolean actual = NEW_FILE.exists();

        // then
        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("null 로 인스턴스 생성 테스트")
    @Test
    void testWithNull() {
        Assertions.assertThatThrownBy(()->new FileResourceCloser(null))
                .isInstanceOf(InputNullParameterException.class);
    }
}