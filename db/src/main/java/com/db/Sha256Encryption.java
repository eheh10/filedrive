package com.db;

import com.db.exception.InputNullParameterException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha256Encryption {
    private static final Sha256Encryption INSTANCE = new Sha256Encryption(getSha256MessageDigest());

    private final MessageDigest messageDigest;

    private Sha256Encryption(MessageDigest messageDigest) {
        if (messageDigest == null) {
            throw new InputNullParameterException();
        }
        this.messageDigest = messageDigest;
    }

    private static MessageDigest getSha256MessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sha256Encryption getInstance() {
        return INSTANCE;
    }

    public String encrypt(String text) {
        messageDigest.reset();
        messageDigest.update(text.getBytes());

        byte[] encrypted = messageDigest.digest();

        return new String(Base64.getEncoder().encode(encrypted));
    }
}
