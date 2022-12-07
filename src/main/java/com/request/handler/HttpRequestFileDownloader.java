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
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    private static final Path DEFAULT_PATH = Paths.get("src","main","resources","uploaded-file");
    private static final PropertyFinder PROPERTY_FINDER = new PropertyFinder();
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private static final UserFiles USER_FILES = new UserFiles();
    private static final FileDownloads FILE_DOWNLOADS = new FileDownloads();
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final String FILE_FILED_NAME = "fileName";

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

        List<File> downloadFiles = searchFiles(bodyQueryString.toString(),FILE_FILED_NAME,userUid);

        if (downloadFiles.size() == 0) {
            throw new InvalidHttpRequestInputException();
        }

        boolean isZipFile = downloadFiles.size() > 1;

        File responseFile = isZipFile? zippingFiles(downloadFiles) : downloadFiles.get(0);

        try {
            recordDownload(userUid,downloadFiles.size());
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
                .append(HttpResponseStatus.CODE_200.message()).append("\n");

        if (isZipFile) {
            response.append("Content-Type: application/zip;charset=euc-kr\n");
        } else {
            response.append("Content-Type: application/octet-stream;charset=euc-kr\n");
        }

        response.append("Content-Disposition: attachment; filename=\"")
                .append(responseFile.getName())
                .append("\"\n\n");

        HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());

        try {
            InputStream fileIs = new FileInputStream(responseFile);
            InputStream responseIs = isZipFile? fileIs: new ZipInputStream(fileIs);
            HttpMessageStream responseBody = HttpMessageStream.of(responseIs);

            return responseMsg.sequenceOf(responseBody);
        } catch (FileNotFoundException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
        }
    }

    private List<File> searchFiles(String queryString, String fieldName, String userUid) {
        List<File> downloadFiles = new ArrayList<>();

        for(String field : queryString.split("&")) {
            int startIdx = field.indexOf(fieldName + "=");

            if (startIdx == -1) {
                continue;
            }

            String fileName = URLDecoder.decode(
                    field.substring(startIdx + fieldName.length() + 1),
                    StandardCharsets.UTF_8);

            FileDto foundFile = USER_FILES.searchFile(fileName,userUid);
            String targetPath = foundFile.getPath();
            File targetFile = DEFAULT_PATH.resolve(targetPath).toFile();

            if (!targetFile.exists()) {
                throw new NotFoundHttpRequestFileException();
            }

            downloadFiles.add(targetFile);
        }

        return Collections.unmodifiableList(downloadFiles);
    }

    private File zippingFiles(List<File> downloadFiles) {
        File zipFile = new File(DEFAULT_PATH.toFile(), "files.zip");

        try {
            OutputStream zipOs = new FileOutputStream(zipFile);
            BufferedOutputStream zipBos = new BufferedOutputStream(zipOs,8192);
            ZipOutputStream zipOut = new ZipOutputStream(zipBos,StandardCharsets.UTF_8);

            InputStream fileIs;
            BufferedInputStream fileBis;
            byte[] buffer = new byte[8192];
            int len = -1;

            for (File file : downloadFiles) {
                ZipEntry ze = new ZipEntry(file.getName());
                zipOut.putNextEntry(ze);

                fileIs = new FileInputStream(file);
                fileBis = new BufferedInputStream(fileIs,8192);

                while ((len = fileBis.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, len);
                }

                zipOut.closeEntry();
                fileBis.close();
            }
            zipOut.flush();
            zipOut.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return zipFile;
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
