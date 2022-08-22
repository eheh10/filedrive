package com.main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
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

            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is,8192);
            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr,8192);

            String line = "";
            StringBuilder input = new StringBuilder();

            while((line=br.readLine()) != null && !Objects.equals(line,"")) {
                input.append(line).append("\n");
            }
            System.out.println(input.toString());

            StringTokenizer stringTokenizer = new StringTokenizer(input.toString(),"\n");

            String startLine = stringTokenizer.nextToken();
            String api = startLine.split(" ")[1];

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os,8192);
            OutputStreamWriter osw = new OutputStreamWriter(bos,StandardCharsets.UTF_8);

            osw.write("HTTP/1.1 200 OK\n"+
                    "Content-Type: text/html;charset=UTF-8\n"+
                    "\n");

            if(Objects.equals("/",api)){
                osw.write(readFile(Paths.get("src","main","resources","test.html"))+"\n");
            }else {
                StringTokenizer apiTokenizer = new StringTokenizer(api.substring(1),"\\?");
                String path = apiTokenizer.nextToken();

                if (Objects.equals(path,"test")){
                    String values = apiTokenizer.nextToken();
                    String name = "";
                    StringTokenizer valueTokenizer = new StringTokenizer(values,"=");

                    if (Objects.equals("name",valueTokenizer.nextToken())){
                        name = URLDecoder.decode(valueTokenizer.nextToken(),StandardCharsets.UTF_8);
                    }

                    osw.write("안녕하세요 "+name+"님\n");
                }else {
                    osw.write(readFile(Paths.get("src","main","resources","error.html"))+"\n");
                }
            }

            osw.write("\n");

            osw.flush();
            osw.close();
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
