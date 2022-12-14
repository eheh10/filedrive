package com.request.handler;

import com.db.dto.FileDto;
import com.db.exception.InvalidSessionException;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.http.HttpMessageStream;
import com.http.HttpRequestLengthLimiters;
import com.http.RetryHttpRequestStream;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.closer.FileResourceCloser;
import com.http.closer.ResourceCloser;
import com.http.request.HttpRequestPath;
import com.http.request.HttpRequestQueryString;
import com.http.request.handler.HttpRequestHandler;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;
import com.http.template.FileTemplateReplacer;
import com.http.template.TemplateFileStream;
import com.http.template.TemplateNodes;
import com.http.template.TemplateText;
import com.request.FileDownloadHtmlGenerator;
import com.request.HttpRequestPagePath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class FileDownloadPageStream implements HttpRequestHandler {
    private static final SessionStorage SESSION_STORAGE = new SessionStorage();
    private static final UserFiles USER_FILES = new UserFiles();

    @Override
    public HttpResponseStream handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestQueryString queryString, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        // 파일 리스트 가공
        HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
        String sessionId = searchSessionId(cookie);

        String userUid = SESSION_STORAGE.getUserUid(sessionId);
        Set<FileDto> files = USER_FILES.filesOf(userUid);

        // 파일 리스트 html 가공
        FileDownloadHtmlGenerator htmlGenerator = FileDownloadHtmlGenerator.of(files);
        String fileListHtml = htmlGenerator.generate();

        // 템플릿에 파일 리스트 대치
        TemplateNodes templateNodes = new TemplateNodes();
        templateNodes.register("fileList", fileListHtml);

        String pagePath = HttpRequestPagePath.DOWNLOAD.path();
        InputStream pageIs = null;
        try {
            pageIs = new FileInputStream(pagePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        TemplateFileStream templateFile = TemplateFileStream.of(pageIs);
        Path replacedFile = Path.of("src", "main", "resources", "fileListPage.html");

        try (FileTemplateReplacer replacer = FileTemplateReplacer.of(templateFile, replacedFile);
        ) {
            replacer.replace(templateNodes, TemplateText.FILE_LIST_TEMPLATE);
        }

        // http response message 가공
        String headers = "Content-Type: text/html;charset=UTF-8";
        HttpMessageStream responseHeaders = HttpMessageStream.of(headers);

        ResourceCloser releaser = new FileResourceCloser(replacedFile.toFile());
        InputStream bodyInput = null;
        try {
            bodyInput = new FileInputStream(replacedFile.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        HttpMessageStream pageHtml = HttpMessageStream.of(bodyInput, releaser);

        return HttpResponseStream.from(
                HttpResponseStatus.CODE_200,
                responseHeaders,
                pageHtml
        );
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
        throw new InvalidSessionException();
    }
}
