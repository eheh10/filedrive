package com.api;

import com.request.BodyLineGenerator;

import java.io.IOException;

public interface HttpApi {
    String get();
    String post(BodyLineGenerator bodyLineGenerator) throws IOException;
}
