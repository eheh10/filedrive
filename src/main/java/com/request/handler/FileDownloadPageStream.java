package com.request.handler;

import com.db.dto.FileDto;
import com.http.*;
import com.http.exception.InputNullParameterException;
import com.db.exception.InvalidSessionException;
import com.http.header.HttpHeaderField;
import com.http.header.HttpHeaders;
import com.http.releaser.FileResourceCloser;
import com.http.releaser.ResourceCloser;
import com.http.request.handler.HttpRequestHandler;
import com.request.FileDownloadHtmlGenerator;
import com.request.HttpRequestPagePath;
import com.http.request.HttpRequestPath;
import com.http.response.HttpResponseStatus;
import com.db.table.SessionStorage;
import com.db.table.UserFiles;
import com.http.template.FileTemplateReplacer;
import com.http.template.TemplateFileStream;
import com.http.template.TemplateNodes;
import com.http.template.TemplateText;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class FileDownloadPageStream implements HttpRequestHandler {
    private final SessionStorage sessionStorage = new SessionStorage();
    private final UserFiles userFiles = new UserFiles();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, RetryHttpRequestStream bodyStream, HttpRequestLengthLimiters requestLengthLimiters) {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        try {
            // 파일 리스트 가공
            HttpHeaderField cookie = httpHeaders.findProperty("Cookie");
            String sessionId = searchSessionId(cookie);

            int userNum = sessionStorage.getUserNum(sessionId);
            Set<FileDto> files = userFiles.filesOf(userNum);

            // 파일 리스트 html 가공
            FileDownloadHtmlGenerator htmlGenerator = FileDownloadHtmlGenerator.of(files);
            String fileListHtml = htmlGenerator.generate();

            // 템플릿에 파일 리스트 대치
            TemplateNodes templateNodes = new TemplateNodes();
            templateNodes.register("fileList",fileListHtml);

            String pagePath = HttpRequestPagePath.DOWNLOAD.path();
            InputStream pageIs = new FileInputStream(pagePath);
            TemplateFileStream templateFile = TemplateFileStream.of(pageIs);
            Path replacedFile = Path.of("src","main","resources", "fileListPage.html");

            try (FileTemplateReplacer replacer = FileTemplateReplacer.of(templateFile,replacedFile);
            ) {
                replacer.replace(templateNodes, TemplateText.ERROR_TEMPLATE);
            }

            // http response message 가공
            StringBuilder response = new StringBuilder();

            response.append("HTTP/1.1 ")
                    .append(HttpResponseStatus.CODE_200.code()).append(" ")
                    .append(HttpResponseStatus.CODE_200.message()).append("\n")
                    .append("Content-Type: text/html;charset=UTF-8\n\n");

            HttpMessageStreams responseMsg = HttpMessageStreams.of(response.toString());

            ResourceCloser releaser = new FileResourceCloser(replacedFile.toFile());
            InputStream bodyInput = new FileInputStream(replacedFile.toFile());
            HttpMessageStream pageHtml = HttpMessageStream.of(bodyInput,releaser);

            return responseMsg.sequenceOf(pageHtml);
        } catch (FileNotFoundException e) {
            return createRedirectionResponse(HttpResponseStatus.CODE_500);
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
        throw new InvalidSessionException();
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
