package com.main;

import com.api.HttpRequestBodyFileCreator;
import com.api.HttpRequestHandlers;
import com.generator.HttpStringGenerator;
import com.method.HttpRequestMethod;
import com.path.HttpRequestPath;
import com.response.HttpRequestTransport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main2 {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        HttpRequestHandlers handlers = new HttpRequestHandlers();
        handlers.register(HttpRequestPath.of("/test"), HttpRequestMethod.POST,new HttpRequestBodyFileCreator());

        while(true) {
            Socket socket = serverSocket.accept();

            HttpStringGenerator generator = HttpStringGenerator.of(socket.getInputStream());
            HttpRequestTransport response = new HttpRequestTransport();

            String responseMsg = response.transport(generator,handlers);
            if (responseMsg == null) {
                continue;
            }

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os,8192);
            OutputStreamWriter bsw = new OutputStreamWriter(bos,StandardCharsets.UTF_8);

            bsw.write(responseMsg);

            bsw.flush();
            bsw.close();
        }
    }
}
