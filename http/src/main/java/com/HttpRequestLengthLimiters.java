package com;

import com.exception.InputNullParameterException;

public class HttpRequestLengthLimiters {
    private final HttpLengthLimiter requestHeadersLengthLimit;
    private final HttpLengthLimiter requestBodyLengthLimit;


    private HttpRequestLengthLimiters(HttpLengthLimiter requestHeadersLengthLimit, HttpLengthLimiter requestBodyLengthLimit) {
        if (requestHeadersLengthLimit == null || requestBodyLengthLimit == null) {
            throw new InputNullParameterException();
        }

        this.requestHeadersLengthLimit = requestHeadersLengthLimit;
        this.requestBodyLengthLimit = requestBodyLengthLimit;
    }

    public static HttpRequestLengthLimiters from(int requestHeadersLimit, int requestBodyLimit) {
        HttpLengthLimiter requestHeadersLengthLimit = new HttpLengthLimiter(requestHeadersLimit);
        HttpLengthLimiter requestBodyLengthLimit = new HttpLengthLimiter(requestBodyLimit);

        return new HttpRequestLengthLimiters(requestHeadersLengthLimit,requestBodyLengthLimit);
    }

    public void accumulateHeadersLength(int length) {
        requestHeadersLengthLimit.accumulate(length);
    }


    public void accumulateBodyLength(int length) {
        requestBodyLengthLimit.accumulate(length);
    }
}
