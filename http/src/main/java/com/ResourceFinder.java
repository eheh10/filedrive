package com;

import com.exception.NotFoundResourceException;
import com.request.HttpRequestPath;

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
                    .getResource(filePath.getValue().toString());

            if (resource == null) {
                throw new NotFoundResourceException();
            }

            StringStream fileStream = StringStream.of(resource.openStream());
            HttpMessageStream fileStreams = HttpMessageStream.of(fileStream);

            return fileStreams;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
