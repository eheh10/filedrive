package com.main;

import com.response.ResponseMsgCreator;

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

        while(true) {
            Socket socket = serverSocket.accept();

            ResponseMsgCreator response = ResponseMsgCreator.of(socket.getInputStream());
            if (response == null) {
                continue;
            }

            String responseMsg = response.create();
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
