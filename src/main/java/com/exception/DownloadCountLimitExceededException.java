package com.exception;

public class DownloadCountLimitExceededException extends RuntimeException {
    public DownloadCountLimitExceededException() {
    }

    public DownloadCountLimitExceededException(String message) {
        super(message);
    }

    public DownloadCountLimitExceededException(Exception e) {
        super(e);
    }
}
