package request.handler;

import com.*;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.request.handler.HttpRequestHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpResourceStream implements HttpRequestHandler {
    private final ResourceFinder resourceFinder = new ResourceFinder();
    private final HttpRequestPath defaultPath = HttpRequestPath.of("/images");

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStreams bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        HttpRequestPath requestFilePath = defaultPath.combine(httpRequestPath);

        String headers = "Content-Type: "+requestFilePath.contentType()+"\n\n";
        InputStream headerInput = new ByteArrayInputStream(headers.getBytes(StandardCharsets.UTF_8));
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpMessageStreams responseHeaders = HttpMessageStreams.of(headerGenerator);

        HttpMessageStream fileStream = resourceFinder.findGetRequestResource(requestFilePath);
        HttpMessageStreams response = responseHeaders.sequenceOf(fileStream);

        return response;
    }
}
