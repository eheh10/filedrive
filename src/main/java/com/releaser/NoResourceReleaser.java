package com.releaser;

public class NoResourceReleaser implements ResourceReleaser{
    @Override
    public boolean release() {
        return true;
    }
}
