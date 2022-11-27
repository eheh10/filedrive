package com.request.handler;

import com.db.DbPropertyFinder;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.*;
import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.exception.NotAllowedFileExtensionException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.db.table.Users;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestFileUploader implements HttpRequestHandler {
    private static final Path DIRECTORY_PATH = Paths.get("src","main","resources","uploaded-file");
    private static final DbPropertyFinder PROPERTY_FINDER = new DbPropertyFinder();
    private final HttpPropertyFinder finder = new HttpPropertyFinder();
    private final SessionStorage sessionStorage = new SessionStorage();
    private final Users users = new Users();
    private final UserFiles userFiles = new UserFiles();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        int userNum = sessionStorage.getUserNum(sessionId);
        UserDto userDto = users.find_BY_NUM(userNum);

        Path savePath = DIRECTORY_PATH.resolve(String.valueOf(userNum));
        try {
            if (!savePath.toFile().exists() || !savePath.toFile().isDirectory()) {
                Files.createDirectory(savePath);
            }

            HttpHeaderField contentType = httpHeaders.findProperty("Content-Type");
            String fileBoundary = parsingBoundary(contentType);
            String lastBoundary = fileBoundary+"--";

            BufferedWriter bw = null;
            boolean writeFileBodyMode = false;

            String filename = "";
            int fileSize = 0;

            try {
                while (bodyStream.hasMoreString()) {
                    String line = bodyStream.generateLine();
                    requestLengthLimiters.accumulateBodyLength(line.length());

                    if (Objects.equals(line, lastBoundary)) {
                        insertFile(userDto, filename, fileSize);
                        break;
                    }

                    if (Objects.equals(line, fileBoundary)) {
                        if (fileSize > 0) {
                            insertFile(userDto, filename, fileSize);
                            fileSize = 0;
                            writeFileBodyMode = false;
                        }
                        continue;
                    }

                    if (writeFileBodyMode) {
                        fileSize += line.length();
                        bw.write(line);
                        continue;
                    }

                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }

                    filename = parsingFilename(line, finder);

                    bw = generateNewFileWriter(savePath.resolve(filename));
                    if (bw == null) {
                        return createRedirectionResponse(HttpResponseStatus.CODE_500);
                    }

                    passContentDisposition(bodyStream);

                    writeFileBodyMode = true;
                }
            } catch (StorageCapacityExceededException e) {
                return createRedirectionResponse(HttpResponseStatus.CODE_400);
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
        }

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_303.code()).append(" ")
                .append(HttpResponseStatus.CODE_303.message()).append("\n")
                .append("Location: http://localhost:7777/page/upload\n");

        return HttpMessageStreams.of(response.toString());
    }

    private void insertFile(UserDto userDto, String filename, int fileSize) {
        int usedCapacity = userDto.getUsageCapacity();

        if (usedCapacity > PROPERTY_FINDER.getStorageCapacity()) {
            throw new StorageCapacityExceededException();
        }

        FileDto fileDto = FileDto.builder()
                .name(filename)
                .path(Paths.get(String.valueOf(userDto.getNum())).resolve(filename).toString())
                .size(fileSize)
                .build();

        userFiles.insert(userDto, fileDto);
        users.useStorageCapacity(userDto, fileDto);
    }

    private String parsingFilename(String contentDisposition, HttpPropertyFinder finder) {
        String filenameWithQuotes = contentDisposition.split("filename=")[1];
        String filename = filenameWithQuotes.substring(1,filenameWithQuotes.length()-1);

        String extension = filename.split("\\.")[1];
        if (isNotAllowedExtension(finder,extension)) {
            throw new NotAllowedFileExtensionException();
        }

        return filename;
    }

    private BufferedWriter generateNewFileWriter(Path filePath) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath.toFile());
        } catch (FileNotFoundException e) {
            return null;
        }
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);

        return new BufferedWriter(osw,8192);
    }

    private void passContentDisposition(RetryHttpRequestStream generator) {
        while(generator.hasMoreString()) {
            String content = generator.generateLine();
            if (Objects.equals(content,"")) {
                return;
            }
        }
    }

    private String parsingBoundary(HttpHeaderField contentType) {
        StringBuilder boundary = new StringBuilder();
        boundary.append("--");

        for(String value:contentType.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx),"boundary")) {
                boundary.append(value.substring(delIdx+1));
                break;
            }
            throw new NotFoundHttpHeadersPropertyException();
        }

        return boundary.toString();
    }

    private boolean isNotAllowedExtension(HttpPropertyFinder finder, String targetExtension) {
        for(String extension : finder.notAllowedFileExtension()) {
            if (Objects.equals(extension,targetExtension)) {
                return true;
            }
        }

        return false;
    }

    private String searchSessionId(HttpHeaderField cookie) {
        for(String value:cookie.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx), SessionStorage.SESSION_FIELD_NAME)) {
                return value.substring(delIdx+1);
            }
        }
        throw new NotFoundHttpHeadersPropertyException();
    }

    private HttpMessageStreams createRedirectionResponse(HttpResponseStatus status) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(status.code()).append(" ")
                .append(status.message()).append("\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);

        return HttpMessageStreams.of(responseStream);
    }
}
