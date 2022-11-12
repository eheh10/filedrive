package com.main;

import com.Bootstrap;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import com.request.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        boolean requiredLogin = true;

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.registerHandler(HttpRequestPath.of("/signUp"), HttpRequestMethod.POST, new HttpRequestUserCreator());
        bootstrap.registerHandler(HttpRequestPath.of("/login"), HttpRequestMethod.POST, new HttpRequestUserFinder());
        bootstrap.registerHandler(HttpRequestPath.of("/upload"), HttpRequestMethod.POST, new HttpRequestFileUploader());
        bootstrap.registerHandler(HttpRequestPath.of("/download"), HttpRequestMethod.POST, new HttpRequestLoginDecorator(new HttpRequestFileUploader()));
        bootstrap.registerHandler(HttpRequestPath.of("/page/login"), HttpRequestMethod.GET, new HttpRequestPageStream(!requiredLogin));
        bootstrap.registerHandler(HttpRequestPath.of("/page/upload"), HttpRequestMethod.GET, new HttpRequestPageStream(requiredLogin));
        bootstrap.registerHandler(HttpRequestPath.ofResourcePath(), HttpRequestMethod.GET, new HttpResourceStream());

        bootstrap.start();
    }

}
