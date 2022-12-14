package com.http.closer;

public class NoResourceCloser implements ResourceCloser {
    @Override
    public boolean close() {
        return true;
    }
}
