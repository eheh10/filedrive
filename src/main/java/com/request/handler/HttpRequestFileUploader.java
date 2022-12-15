package com.request.handler;

import com.db.DbPropertyFinder;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.*;
import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotAllowedFileExtensionException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.db.table.Users;
import com.http.response.HttpResponseStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class HttpRequestFileUploader implements HttpRequestHandler {
    private static final Path DIRECTORY_PATH = Paths.get("src","main","resources","uploaded-file"); //property
    private static final DbPropertyFinder PROPERTY_FINDER = DbPropertyFinder.getInstance();
    private static final HttpPropertyFinder FINDER = HttpPropertyFinder.getInstance();
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private static final Users USERS = new Users();
    private static final UserFiles USER_FILES = new UserFiles();

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null || queryString == null || requestLengthLimiters == null) {
            throw new InputNullParameterException(
                    "httpRequestPath: "+httpRequestPath+"\n"+
                            "httpHeaders: "+httpHeaders+"\n"+
                            "bodyStream: "+bodyStream+"\n"+
                            "queryString: "+queryString+"\n"+
                            "requestLengthLimiters: "+requestLengthLimiters+"\n"
            );
        }

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        String userUid = SESSION_STORAGE.getUserUid(sessionId);
        UserDto userDto = USERS.searchByUid(userUid);

        Path savePath = DIRECTORY_PATH.resolve(userUid);
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

                    filename = parsingFilename(line, FINDER);

                    bw = generateNewFileWriter(savePath.resolve(filename));
                    if (bw == null) {
                        throw new RuntimeException();
                    }

                    passContentDisposition(bodyStream);

                    writeFileBodyMode = true;
                }
            } catch (StorageCapacityExceededException e) {
                throw new InvalidHttpRequestInputException();
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        HttpMessageStream responseHeaders = HttpMessageStream.of("Location: http://localhost:7777/page/upload");

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_303,
                responseHeaders,
                HttpMessageStream.empty()
        );
    }

    private void insertFile(UserDto userDto, String filename, int fileSize) {
        int usedCapacity = userDto.getUsageCapacity();

        if (usedCapacity > PROPERTY_FINDER.getStorageCapacity()) {
            throw new StorageCapacityExceededException();
        }

        FileDto fileDto = FileDto.builder()
                .uid(UUID.randomUUID().toString())
                .name(filename)
                .path(Paths.get(userDto.getUid()).resolve(filename).toString())
                .size(fileSize)
                .build();

        USER_FILES.insert(userDto, fileDto);
        USERS.useStorageCapacity(userDto, fileDto);
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

}
