package com;

public enum HttpLimitLength {
    REQUEST_BODY(100),
    RESPONSE_BODY(200);

    private final int limitLength;
    HttpLimitLength(int limitLength) {
        this.limitLength = limitLength;
    }

    public HttpLengthLimiter createLimiter() {
        return new HttpLengthLimiter(limitLength);
    }
}
