package request.handler;

import com.HttpLengthLimiter;
import com.HttpsStream;
import com.StringStream;
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
    public HttpsStream handle(HttpHeaders httpHeaders, HttpsStream generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException {
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
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpsStream responseHeaders = HttpsStream.of(headerGenerator);

        InputStream bodyInput = new FileInputStream(targetPath.toFile());
        StringStream bodyGenerator = StringStream.of(bodyInput);
        HttpsStream responseBody = HttpsStream.of(bodyGenerator);

        return responseHeaders.sequenceOf(responseBody);
    }
}
