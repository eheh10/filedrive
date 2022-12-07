package com.http.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyRequestException extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(EmptyRequestException.class);

    public EmptyRequestException() {
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(Exception e) {
        super(e);
        LOG.error("error",e);
    }
}
