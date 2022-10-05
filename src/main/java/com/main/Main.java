package com.main;

import com.Bootstrap;
import com.request.HttpRequestMethod;
import com.request.HttpRequestPath;
import request.handler.HttpRequestBodyFileCreator;
import request.handler.HttpRequestFileUploader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.registerHandler(HttpRequestPath.of("/test"), HttpRequestMethod.POST,new HttpRequestBodyFileCreator());
        bootstrap.registerHandler(HttpRequestPath.of("/upload"), HttpRequestMethod.POST,new HttpRequestFileUploader());
        bootstrap.start();
    }
}
