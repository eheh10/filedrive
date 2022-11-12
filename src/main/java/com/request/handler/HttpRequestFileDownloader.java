package com.request.handler;

import com.HttpMessageStream;
import com.HttpMessageStreams;
import com.StringStream;
import com.dto.FileDto;
import com.exception.InputNullParameterException;
import com.exception.NotFoundHttpRequestFileException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.table.FileTable;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private final FileTable fileTable = new FileTable();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStream bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        String fileName = "test1.txt";

        Path defaultPath = Paths.get("src","main","resources","uploaded-file");
        Path targetPath = defaultPath.resolve(fileName);
        if (Files.notExists(targetPath)) {
            throw new NotFoundHttpRequestFileException();
        }

        FileDto fileDto = FileDto.builder()
                .name(fileName)
                .path(targetPath.toString())
                .build();

        fileTable.insert(fileDto);

        StringBuilder headers = new StringBuilder();
        headers.append("Content-Type: application/octet-stream;charset=euc-kr\n")
                .append("Content-Disposition: attachment; filename=\"")
                .append(fileName)
                .append("\"\n\n");

        InputStream headerInput = new ByteArrayInputStream(headers.toString().getBytes(StandardCharsets.UTF_8));
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpMessageStreams responseHeaders = HttpMessageStreams.of(headerGenerator);

        InputStream bodyInput = new FileInputStream(targetPath.toFile());
        StringStream bodyGenerator = StringStream.of(bodyInput);
        HttpMessageStream responseBody = HttpMessageStream.of(bodyGenerator);

        HttpMessageStreams response = responseHeaders.sequenceOf(responseBody);

        return response;
    }
}
