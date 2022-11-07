package request.handler;

import com.HttpMessageStreams;
import com.StringStream;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.request.handler.HttpRequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestBodyFileCreator implements HttpRequestHandler {
    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStreams bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        String headers = "Content-Type: text/html;charset=UTF-8\n\n";
        InputStream headerInput = new ByteArrayInputStream(headers.getBytes(StandardCharsets.UTF_8));
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpMessageStreams responseHeaders = HttpMessageStreams.of(headerGenerator);

        Path directoryPath = Paths.get(System.getProperty("user.home"),"fileDrive");

        if (!Files.isDirectory(directoryPath)) {
            Files.createDirectory(directoryPath);
        }

        Path filePath = directoryPath.resolve("test.txt");
        OutputStream os = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);

        try (BufferedWriter bw = new BufferedWriter(osw,8192);) {

            while (bodyStream.hasMoreString()) {
                bw.write(bodyStream.generate());
            }

            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseHeaders;
    }
}
