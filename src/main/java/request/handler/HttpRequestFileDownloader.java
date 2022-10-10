package request.handler;

import com.HttpLengthLimiter;
import com.HttpStreamGenerator;
import com.InputStreamGenerator;
import com.exception.NotFoundHttpRequestFileException;
import com.exception.InputNullParameterException;
import com.header.HttpHeaders;
import com.request.handler.HttpRequestHandler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpRequestFileDownloader implements HttpRequestHandler {
    @Override
    public HttpStreamGenerator handle(HttpHeaders httpHeaders, HttpStreamGenerator generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException {
        if (httpHeaders == null || generator == null || requestBodyLengthLimit == null) {
            throw new InputNullParameterException();
        }

        String fileName = "test1.txt";

        Path defaultPath = Paths.get("src","main","resources","uploaded-file");
        Path targetPath = defaultPath.resolve(fileName);
        if (Files.notExists(targetPath)) {
            throw new NotFoundHttpRequestFileException();
        }

        StringBuilder headers = new StringBuilder();
        headers.append("Content-Type: application/octet-stream;charset=euc-kr\n")
                .append("Content-Disposition: attachment; filename=\"")
                .append(fileName)
                .append("\"\n\n");

        InputStream headerInput = new ByteArrayInputStream(headers.toString().getBytes(StandardCharsets.UTF_8));
        InputStreamGenerator headerGenerator = InputStreamGenerator.of(headerInput);
        HttpStreamGenerator responseHeaders = HttpStreamGenerator.of(headerGenerator);

        InputStream bodyInput = new FileInputStream(targetPath.toFile());
        InputStreamGenerator bodyGenerator = InputStreamGenerator.of(bodyInput);
        HttpStreamGenerator responseBody = HttpStreamGenerator.of(bodyGenerator);

        return responseHeaders.sequenceOf(responseBody);
    }
}
