package com.main;

import com.response.OutputStreamSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        while(true) {
            Socket socket = serverSocket.accept();

            ResponseMessageCreator creator = new ResponseMessageCreator();
            String responseMessage = creator.create(socket.getInputStream());

            if (responseMessage.isBlank()) {
                continue;
            }

            OutputStreamSender outputStreamSender = OutputStreamSender.of(socket.getOutputStream());
            outputStreamSender.send(responseMessage);
            outputStreamSender.close();
        }
    }
}
