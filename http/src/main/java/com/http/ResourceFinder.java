package com.http;

import com.http.exception.NotFoundResourceException;
import com.http.request.HttpRequestPath;
import com.http.template.TemplateFileStream;

import java.io.IOException;
import java.net.URL;

public class ResourceFinder {

    public TemplateFileStream findTemplate(String filename) {
        try {
            URL resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("template/"+filename);

            if (resource == null) {
                throw new NotFoundResourceException();
            }

            return TemplateFileStream.of(resource.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMessageStream findGetRequestResource(HttpRequestPath filePath) {
        try {
            URL resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(filePath.getPath());

            if (resource == null) {
                throw new NotFoundResourceException();
            }

            ResourceStream fileStream = ResourceStream.of(resource.openStream());
            HttpMessageStream fileStreams = HttpMessageStream.of(fileStream);

            return fileStreams;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
