package com.main;

import com.api.TestApi;
import com.request.InputStreamListener;
import com.response.OutputStreamSender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);

        while(true) {
            Socket socket = serverSocket.accept();

            InputStreamListener inputstreamListener = InputStreamListener.of(socket.getInputStream());
            String requestInput = inputstreamListener.listen();
            System.out.println(requestInput);

            StringTokenizer stringTokenizer = new StringTokenizer(requestInput,"\n");

            if (stringTokenizer.countTokens() < 1){
                continue;
            }

            String startLine = stringTokenizer.nextToken();
            String api = startLine.split(" ")[1];

            StringBuilder responseMessage = new StringBuilder();

            responseMessage.append("HTTP/1.1 200 OK\n"+
                    "Content-Type: text/html;charset=UTF-8\n"+
                    "\n");

            if(Objects.equals("/",api)){
                responseMessage.append(readFile(Paths.get("src","main","resources","test.html"))).append("\n");
            }else {
                StringTokenizer apiTokenizer = new StringTokenizer(api.substring(1),"\\?");
                String path = apiTokenizer.nextToken();

                if (Objects.equals(path,"test")){
                    TestApi testApi = new TestApi();
                    String values = apiTokenizer.nextToken();
                    String testResponse = testApi.getResponseMessage(values);

                    responseMessage.append(testResponse).append("\n");
                }else {
                    responseMessage.append(readFile(Paths.get("src","main","resources","error.html"))).append("\n");
                }
            }

            OutputStreamSender outputStreamSender = OutputStreamSender.of(socket.getOutputStream());
            outputStreamSender.send(responseMessage.toString());
            outputStreamSender.close();
        }
    }

    public static String readFile(Path path) throws IOException {
        InputStream is = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(is,8192);
        InputStreamReader isr = new InputStreamReader(bis,StandardCharsets.UTF_8);

        int len = 0;
        char[] buffer = new char[100];
        StringBuilder output = new StringBuilder();
        while((len=isr.read(buffer))!=-1){
            output.append(buffer,0,len);
        }

        return output.toString();
    }
}
