package com.releaser;

public class NoResourceCloser implements ResourceCloser {
    @Override
    public boolean close() {
        return true;
    }
}
