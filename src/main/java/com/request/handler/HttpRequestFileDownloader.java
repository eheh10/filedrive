package com.request.handler;

import com.db.dto.FileDownloadDto;
import com.db.dto.FileDto;
import com.db.exception.VersionUpdatedException;
import com.db.table.FileDownloads;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.exception.DownloadCountLimitExceededException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.http.*;
import com.http.exception.InputNullParameterException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import com.http.exception.NotFoundHttpRequestFileException;
import com.http.exception.NotFoundQueryStringValueException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.property.PropertyFinder;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private static final Path DEFAULT_PATH = Paths.get("src","main","resources","uploaded-file");
    private static final PropertyFinder PROPERTY_FINDER = new PropertyFinder();
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private static final UserFiles USER_FILES = new UserFiles();
    private static final FileDownloads FILE_DOWNLOADS = new FileDownloads();
    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        StringBuilder bodyQueryString = new StringBuilder();

        while(bodyStream.hasMoreString()) {
            bodyQueryString.append(bodyStream.generate());
        }

        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        String userUid = SESSION_STORAGE.getUserUid(sessionId);

        String fileName = searchFileName(bodyQueryString.toString(), "fileName");
        FileDto foundFile = USER_FILES.searchFile(fileName,userUid);
        String targetPath = foundFile.getPath();
        File targetFile = DEFAULT_PATH.resolve(targetPath).toFile();

        if (!targetFile.exists()) {
            throw new NotFoundHttpRequestFileException();
        }

        try {
            recordDownload(userUid);
        } catch (DownloadCountLimitExceededException e) {
            StringBuilder response = new StringBuilder();

            response.append("HTTP/1.1 ")
                    .append(HttpResponseStatus.CODE_200.code()).append(" ")
                    .append(HttpResponseStatus.CODE_200.message()).append("\n")
                    .append("Content-Type: application/json;charset=utf-8\n\n");

            Map responseBody = Map.of(
                    "statusCode",400,
                    "message","일일 다운로드 횟수 초과"
            );

            String responseJson = convertJson(responseBody);
            response.append(responseJson);

            HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());
            return responseMsg;
        }

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ")
                .append(HttpResponseStatus.CODE_200.code()).append(" ")
                .append(HttpResponseStatus.CODE_200.message()).append("\n")
                .append("Content-Type: application/octet-stream;charset=euc-kr\n")
                .append("Content-Disposition: attachment; filename=\"")
                .append(fileName)
                .append("\"\n\n");

        HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());

        try {
            InputStream fileIs = new FileInputStream(targetFile);
            HttpMessageStream responseBody = HttpMessageStream.of(fileIs);

            return responseMsg.sequenceOf(responseBody);
        } catch (FileNotFoundException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
        }
    }

    private String convertJson(Map mapValue) {
        try {
            return JSON_MAPPER.writeValueAsString(mapValue);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void recordDownload(String userUid) {
        LocalDate today = LocalDate.now();
        FileDownloadDto todayDownload = FILE_DOWNLOADS.searchDownload(userUid,today);

        if (todayDownload == null) {
            todayDownload = FileDownloadDto.builder()
                    .uid(UUID.randomUUID().toString())
                    .userUid(userUid)
                    .downloadDate(today)
                    .count(0)
                    .version(1)
                    .build();

            FILE_DOWNLOADS.insert(todayDownload);
        }

        checkDownloadLimit(todayDownload);

        int retry = 3;
        while (retry-- > 0){
            try {
                FILE_DOWNLOADS.countDownload(todayDownload);
                break;
            } catch (VersionUpdatedException e) {
                todayDownload = FILE_DOWNLOADS.searchDownload(userUid, today);
                checkDownloadLimit(todayDownload);
                continue;
            }
        }
    }

    private void checkDownloadLimit(FileDownloadDto todayDownload) {
        if (todayDownload.getCount() + 1 > PROPERTY_FINDER.fileDownloadCountLimit()) {
            throw new DownloadCountLimitExceededException();
        }
    }

    private String searchFileName(String query, String target) {
        int startIdx = query.indexOf(target + "=");

        if (startIdx == -1) {
            throw new NotFoundQueryStringValueException();
        }

        return URLDecoder.decode(
                query.substring(startIdx + target.length() + 1),
                StandardCharsets.UTF_8);
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
