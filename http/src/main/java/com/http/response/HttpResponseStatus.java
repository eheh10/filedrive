package com.http.response;

import com.http.exception.*;

public enum HttpResponseStatus {
    CODE_200("OK"),
    CODE_303("See Other"),
    CODE_304("Not Modified"),
    CODE_400("Bad Request"),
    CODE_401("Unauthorized"),
    CODE_404("Not Found"),
    CODE_405("Method Not Allowed"),
    CODE_413("Request Entity Too Large"),
    CODE_431("Request Header Too Large"),
    CODE_500("Server Error");

    private final String message;
    HttpResponseStatus(String message) {

        this.message = message;
    }

    public static HttpResponseStatus httpResponseStatusOf(Exception exception) {
        Class clz = exception.getClass();

        if (clz.isInstance(InvalidHttpRequestInputException.class)) {
            return CODE_400;
        }

        if (clz.isInstance(RequiredLoginException.class)) {
            return CODE_401;
        }

        if (clz.isInstance(NotFoundHttpPathException.class)) {
            return CODE_404;
        }

        if (clz.isInstance(NotAllowedHttpMethodException.class)) {
            return CODE_405;
        }

        if (clz.isInstance(ExceedingHttpLengthLimitException.class)) {
            return CODE_431;
        }

        if (clz.isInstance(InputNullParameterException.class) ||
                clz.isInstance(MustBePositiveNumberException.class) ||
                clz.isInstance(NoMoreHttpContentException.class) ||
                clz.isInstance(NotFoundHttpHeadersPropertyException.class)) {
            return CODE_500;
        }

        exception.printStackTrace();
        return CODE_500;
    }

    public String code() {
        return this.name().substring(5);
    }

    public String message() {
        return message;
    }
}
