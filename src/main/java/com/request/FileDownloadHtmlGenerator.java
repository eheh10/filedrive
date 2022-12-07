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

            html.append("<p>")
                    .append("<input type=\"checkbox\" name=\"fileName\" value=\"").append(fileName).append("\">").append(fileName)
                    .append("</p>\n");
        }

        return html.toString();
    }
}
