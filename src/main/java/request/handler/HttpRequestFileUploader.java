package request.handler;

import com.*;
import com.exception.InputNullParameterException;
import com.exception.NotAllowedFileExtensionException;
import com.exception.NotFoundHttpHeadersPropertyException;
import com.header.HttpHeaderField;
import com.header.HttpHeaders;
import com.request.HttpRequestPath;
import com.request.handler.HttpRequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HttpRequestFileUploader implements HttpRequestHandler {
    private static final Path DIRECTORY_PATH = Paths.get("src","main","resources","uploaded-file");
    private final PropertyFinder finder = new PropertyFinder();

    @Override
    public HttpMessageStreams handle(HttpRequestPath httpRequestPath, HttpHeaders httpHeaders, HttpMessageStreams bodyStream) throws IOException {
        if (httpRequestPath == null || httpHeaders == null || bodyStream == null) {
            throw new InputNullParameterException();
        }

        String headers = "Content-Type: text/html;charset=UTF-8\n\n";
        InputStream headerInput = new ByteArrayInputStream(headers.getBytes(StandardCharsets.UTF_8));
        StringStream headerGenerator = StringStream.of(headerInput);
        HttpMessageStreams responseHeaders = HttpMessageStreams.of(headerGenerator);

//        Properties properties = new Properties();
//        properties.load(new FileInputStream(Path.of("config.properties").toString()));


        if (!Files.isDirectory(DIRECTORY_PATH)) {
            Files.createDirectory(DIRECTORY_PATH);
            System.out.println("directory 생성: " +DIRECTORY_PATH.toAbsolutePath().toString());
        }

        HttpHeaderField contentType = httpHeaders.findProperty("Content-Type");
        String fileBoundary = parsingBoundary(contentType);
        String lastBoundary = fileBoundary+"--";

        BufferedWriter bw = null;
        boolean writeFileBodyMode = false;

        while (bodyStream.hasMoreString()) {
            String line = bodyStream.generateLine();
            System.out.println(line);

            if (Objects.equals(line,lastBoundary)) {
                break;
            }

            if (Objects.equals(line,fileBoundary)) {
                writeFileBodyMode = false;
            }

            if (writeFileBodyMode) {
                bw.write(line);
                continue;
            }

            if (bw != null) {
                bw.flush();
                bw.close();
            }

            String contentDisposition = bodyStream.generateLine();
            String filename = parsingFilename(contentDisposition,finder);

            bw = generateNewFileWriter(filename);

            passContentDisposition(bodyStream);

            writeFileBodyMode = true;
        }

        bw.flush();
        bw.close();

        return responseHeaders;
    }

    private String parsingFilename(String contentDisposition, PropertyFinder finder) {
        String filenameWithQuotes = contentDisposition.split("filename=")[1];
        String filename = filenameWithQuotes.substring(1,filenameWithQuotes.length()-1);

        String extension = filename.split("\\.")[1];
        if (isNotAllowedExtension(finder,extension)) {
            throw new NotAllowedFileExtensionException();
        }

        return filename;
    }

    private BufferedWriter generateNewFileWriter(String filename) throws FileNotFoundException {
        Path filePath = DIRECTORY_PATH.resolve(filename);
        OutputStream os = new FileOutputStream(filePath.toString());
        BufferedOutputStream bos = new BufferedOutputStream(os,8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);

        return new BufferedWriter(osw,8192);
    }

    private void passContentDisposition(HttpMessageStreams generator) throws IOException {
        while(generator.hasMoreString()) {
            String content = generator.generateLine();
            System.out.println(content);
            if (Objects.equals(content,"")) {
                return;
            }
        }
    }

    private String parsingBoundary(HttpHeaderField contentType) {
        StringBuilder boundary = new StringBuilder();
        boundary.append("--");

        for(String value:contentType.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx),"boundary")) {
                boundary.append(value.substring(delIdx+1));
                break;
            }
            throw new NotFoundHttpHeadersPropertyException();
        }

        return boundary.toString();
    }

    private boolean isNotAllowedExtension(PropertyFinder finder, String targetExtension) {
        for(String extension : finder.find(PropertyKey.NOT_ALLOWED_EXTENSION).split(",")) {
            if (Objects.equals(extension,targetExtension)) {
                return true;
            }
        }

        return false;
    }
}
