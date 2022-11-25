package com.request;

import com.db.dto.FileDto;
import com.http.exception.InputNullParameterException;

import java.util.Set;

public class FileDownloadHtmlGenerator {
    private final Set<FileDto> files;

    private FileDownloadHtmlGenerator(Set<FileDto> files) {
        if (files == null) {
            throw new InputNullParameterException();
        }
        this.files = files;
    }

    public static FileDownloadHtmlGenerator of(Set<FileDto> files) {
        if (files == null) {
            throw new InputNullParameterException();
        }
        return new FileDownloadHtmlGenerator(files);
    }

    public String generate() {
        StringBuilder html = new StringBuilder();

        for(FileDto fileDto:files) {
            String fileName = fileDto.getName();

            html.append("<form action=\"http://localhost:7777/download\" method=\"post\">")
                    .append("<p>")
                    .append("<input type=\"text\" name=\"fileName\" value=\"").append(fileName).append("\" readonly>")
                    .append("<input type=\"submit\" value=\"").append("다운로드\">")
                    .append("</p>\n")
                    .append("</form>");
        }

        return html.toString();
    }
}
