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
import com.http.exception.InvalidHttpRequestInputException;
import com.http.exception.NotFoundHttpHeadersPropertyException;
import com.http.exception.NotFoundHttpRequestFileException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.closer.FileResourceCloser;
import com.http.closer.ResourceCloser;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;
import com.property.PropertyFinder;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private static final Path DEFAULT_PATH = Paths.get("src","main","resources","uploaded-file");
    private static final Path TEMP_FILE_PATH = Paths.get("src","main","resources","tmp");
    private static final PropertyFinder PROPERTY_FINDER = new PropertyFinder();
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private static final UserFiles USER_FILES = new UserFiles();
    private static final FileDownloads FILE_DOWNLOADS = new FileDownloads();
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final String FILE_FILED_NAME = "fileName";

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
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

        List<String> downloadFiles = searchFilePaths(bodyQueryString.toString(),FILE_FILED_NAME,userUid);

        int numberOfFiles = downloadFiles.size();

        if (numberOfFiles == 0) {
            throw new InvalidHttpRequestInputException();
        }

        boolean isZipFile = numberOfFiles > 1;

        String responseFilePath = isZipFile? zippingFiles(downloadFiles) : downloadFiles.get(0);
        File responseFile = new File(responseFilePath);

        try {
            recordDownload(userUid,numberOfFiles);
        } catch (DownloadCountLimitExceededException e) {
            HttpMessageStream responseHeaders = HttpMessageStream.of("Content-Type: application/json;charset=utf-8");

            Map responseBody = Map.of(
                    "statusCode",400,
                    "message","일일 다운로드 횟수 초과"
            );

            String json = convertJson(responseBody);
            HttpMessageStream responseJson = HttpMessageStream.of(json);

            return HttpResponseStream.from(HttpResponseStatus.CODE_200,responseHeaders,responseJson);
        }

        String contentType = isZipFile ? "application/zip;charset=euc-kr" : "application/octet-stream;charset=euc-kr";

        StringBuilder headers = new StringBuilder();
        headers.append("Content-Type: ").append(contentType).append("\n")
                .append("Content-Disposition: attachment; filename=\"")
                .append(responseFile.getName());

        HttpMessageStream responseHeaders = HttpMessageStream.of(headers.toString());

        try {

            InputStream responseIs = new FileInputStream(responseFile);
            ResourceCloser resourceCloser = new FileResourceCloser(responseFile);
            HttpMessageStream responseBody = HttpMessageStream.of(responseIs,resourceCloser);

            return HttpResponseStream.from(
                    HttpResponseStatus.CODE_200,
                    responseHeaders,
                    responseBody
            );
        } catch (FileNotFoundException e) {
            return  HttpResponseStream.from(
                    HttpResponseStatus.CODE_500,
                    HttpMessageStream.empty(),
                    HttpMessageStream.empty()
            );
        }
    }

    private List<String> searchFilePaths(String queryString, String fieldName, String userUid) {
        List<String> downloadFiles = new ArrayList<>();

        for(String field : queryString.split("&")) {
            int startIdx = field.indexOf(fieldName + "=");

            if (startIdx == -1) {
                continue;
            }

            String fileName = URLDecoder.decode(
                    field.substring(startIdx + fieldName.length() + 1),
                    StandardCharsets.UTF_8);

            FileDto foundFile = USER_FILES.searchFile(fileName,userUid);
            Path targetPath = DEFAULT_PATH.resolve(foundFile.getPath());


            if (!Files.exists(targetPath)) {
                throw new NotFoundHttpRequestFileException();
            }

            downloadFiles.add(targetPath.toString());
        }

        return Collections.unmodifiableList(downloadFiles);
    }

    private String zippingFiles(List<String> downloadFiles) {
        File zipFile = new File(TEMP_FILE_PATH.toFile(), "files.zip");

        try {
            if (Files.notExists(TEMP_FILE_PATH)) {
                Files.createDirectories(TEMP_FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                OutputStream zipOs = new FileOutputStream(zipFile);
                BufferedOutputStream zipBos = new BufferedOutputStream(zipOs,8192);
                ZipOutputStream zipOut = new ZipOutputStream(zipBos,StandardCharsets.UTF_8);
        ){

            byte[] buffer = new byte[8192];
            int len = -1;

            for (String filePath : downloadFiles) {
                File file = new File(filePath);
                ZipEntry ze = new ZipEntry(file.getName());
                zipOut.putNextEntry(ze);

                try (
                        InputStream fileIs = new FileInputStream(file);
                        BufferedInputStream fileBis = new BufferedInputStream(fileIs,8192);
                ) {
                    while ((len = fileBis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, len);
                        zipOut.flush();
                    }

                    zipOut.closeEntry();
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return zipFile.getPath();
    }

    private String convertJson(Map mapValue) {
        try {
            return JSON_MAPPER.writeValueAsString(mapValue);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void recordDownload(String userUid, int numberOfFiles) {
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

        checkDownloadLimit(todayDownload,numberOfFiles);

        int retry = 3;
        while (retry-- > 0){
            try {
                FILE_DOWNLOADS.countDownload(todayDownload,numberOfFiles);
                break;
            } catch (VersionUpdatedException e) {
                todayDownload = FILE_DOWNLOADS.searchDownload(userUid, today);
                checkDownloadLimit(todayDownload,numberOfFiles);
                continue;
            }
        }
    }

    private void checkDownloadLimit(FileDownloadDto todayDownload, int numberOfFiles) {
        if (todayDownload.getCount() + numberOfFiles > PROPERTY_FINDER.fileDownloadCountLimit()) {
            throw new DownloadCountLimitExceededException();
        }
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
