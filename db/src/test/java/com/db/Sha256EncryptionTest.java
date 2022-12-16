package com.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class Sha256EncryptionTest {
    private static final Sha256Encryption ENCRYPTION = Sha256Encryption.getInstance();
    private final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

    private Sha256EncryptionTest() throws NoSuchAlgorithmException {
    }

    private String encrypt(String text) {
        messageDigest.update(text.getBytes());

        byte[] encrypted = messageDigest.digest();

        return new String(Base64.getEncoder().encode(encrypted));
    }

    @BeforeEach
    void resetMessageDigest() {
        messageDigest.reset();
    }

    @DisplayName("암호화 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"test","hello\nworld","12345","a1b2c3"})
    void testEncryption(String text) {
        // given
        String expected = encrypt(text);

        // when
        String actual = ENCRYPTION.encrypt(text);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}