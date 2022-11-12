package com.request.handler;

import com.*;
import com.dto.FileDto;
import com.dto.UserDto;
import com.exception.InputNullParameterException;
import com.exception.NotAllowedFileExtensionException;
import com.exception.NotFoundHttpHeadersPropertyException;
import com.header.HttpHeaderField;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.table.FileTable;
import com.table.UserTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestFileUploader implements HttpRequestHandler {
    private static final Path DIRECTORY_PATH = Paths.get("src","main","resources","uploaded-file");
    private final PropertyFinder finder = new PropertyFinder();
    private final SessionStorage sessionStorage = new SessionStorage();
    private final UserTable userTable = new UserTable();
    private final FileTable fileTable = new FileTable();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStream bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        System.out.println("====");

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        if(sessionId == null) {
            return createRedirectionResponse(HttpResponseStatus.CODE_304);
        }

        if (!sessionStorage.validSession(sessionId)) {
            return createRedirectionResponse(HttpResponseStatus.CODE_401);
        }

        Cookies cookies = sessionStorage.getCookie(sessionId);
        String cookieId = cookies.searchValue("userId");
        String cookiePwd = cookies.searchValue("userPwd");

        UserDto userDto = UserDto.builder()
                .id(cookieId)
                .pwd(cookiePwd)
                .build();

        if (userTable.IsUnregisteredUser(userDto)) {
            return createRedirectionResponse(HttpResponseStatus.CODE_401);
        }

        if (!Files.isDirectory(DIRECTORY_PATH)) {
            Files.createDirectory(DIRECTORY_PATH);
        }

        HttpHeaderField contentType = httpHeaders.findProperty("Content-Type");
        String fileBoundary = parsingBoundary(contentType);
        String lastBoundary = fileBoundary+"--";

        BufferedWriter bw = null;
        boolean writeFileBodyMode = false;

        String filename = "";
        int fileSize = 0;

        while (bodyStream.hasMoreString()) {
            String line = bodyStream.generateLine();

            if (Objects.equals(line,lastBoundary)) {
                FileDto fileDto = FileDto.builder()
                        .name(filename)
                        .path(DIRECTORY_PATH.resolve(filename).toString())
                        .size(fileSize)
                        .build();
                fileTable.insert(fileDto);
                break;
            }

            if (Objects.equals(line,fileBoundary)) {
                FileDto fileDto = FileDto.builder()
                        .name(filename)
                        .path(DIRECTORY_PATH.resolve(filename).toString())
                        .size(fileSize)
                        .build();
                fileTable.insert(fileDto);
                fileSize = 0;
                writeFileBodyMode = false;
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

            String contentDisposition = bodyStream.generateLine();
            filename = parsingFilename(contentDisposition,finder);

            bw = generateNewFileWriter(filename);

            passContentDisposition(bodyStream);

            writeFileBodyMode = true;
        }

        bw.flush();
        bw.close();

        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: text/html;charset=UTF-8\n");

        InputStream responseIs = new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
        StringStream responseStream = StringStream.of(responseIs);

        return HttpMessageStreams.of(responseStream);
    }

    private String parsingFilename(String contentDisposition, PropertyFinder finder) {
        String filenameWithQuotes = contentDisposition.split("filename=")[1];
        String filename = filenameWithQuotes.substring(1,filenameWithQuotes.length()-1);

        String extension = filename.split("\\.")[1];
        if (isNotAllowedExtension(finder,extension)) {
            throw new NotAllowedFileExtensionException();
        }

        return filename;
    }

    private BufferedWriter generateNewFileWriter(String filename) throws FileNotFoundException {
        Path filePath = DIRECTORY_PATH.resolve(filename);
        OutputStream os = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);

        return new BufferedWriter(osw,8192);
    }

    private void passContentDisposition(HttpMessageStream generator) throws IOException {
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

    private boolean isNotAllowedExtension(PropertyFinder finder, String targetExtension) {
        for(String extension : finder.find(PropertyKey.NOT_ALLOWED_EXTENSION).split(",")) {
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

            if (Objects.equals(value.substring(0,delIdx),SessionStorage.SESSION_ID_NAME)) {
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
