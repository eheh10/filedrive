package request.handler;

import com.HttpLengthLimiter;
import com.HttpStreamGenerator;
import com.exception.NotAllowedFileExtensionException;
import com.exception.NotFoundHttpHeadersPropertyException;
import com.exception.NullException;
import com.header.HttpHeaderField;
import com.header.HttpHeaders;
import com.request.handler.HttpRequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class HttpRequestFileUploader implements HttpRequestHandler {
    @Override
    public HttpStreamGenerator handle(HttpHeaders httpHeaders, HttpStreamGenerator generator, HttpLengthLimiter requestBodyLengthLimit) throws IOException {
        if (httpHeaders == null || generator == null) {
            throw new NullException();
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(Path.of("config.properties").toString()));

        Path directoryPath = Paths.get("src","main","resources","uploaded-file");

        if (!Files.isDirectory(directoryPath)) {
            Files.createDirectory(directoryPath);
            System.out.println("directory 생성: " +directoryPath.toAbsolutePath().toString());
        }

        String fileBoundary = "";
        HttpHeaderField contentType = httpHeaders.findProperty("Content-Type");

        for(String value:contentType.getValues()) {
            int delIdx = value.indexOf('=');
            if (delIdx == -1) {
                continue;
            }

            if (Objects.equals(value.substring(0,delIdx),"boundary")) {
                fileBoundary = "--"+value.substring(delIdx+1);
                break;
            }
            throw new NotFoundHttpHeadersPropertyException();
        }

        BufferedWriter bw = null;

        while (generator.hasMoreString()) {
            String line = generator.generateLine();
            requestBodyLengthLimit.accumulate(line.length());

            if (Objects.equals(line,fileBoundary+"--")) {
                break;
            }

            if (!Objects.equals(line,fileBoundary)) {
                requestBodyLengthLimit.accumulate(line.length());
                bw.write(line);
                continue;
            }

            if (bw != null) {
                bw.flush();
                bw.close();
            }

            String contentDisposition = generator.generateLine();
            String filename = contentDisposition.split("filename=")[1];
            filename = filename.substring(1,filename.length()-1);

            String extension = filename.split("\\.")[1];

            if (isNotAllowedExtension(properties,extension)) {
                throw new NotAllowedFileExtensionException();
            }

            Path filePath = directoryPath.resolve(filename);

            OutputStream os = new FileOutputStream(filePath.toString());
            BufferedOutputStream bos = new BufferedOutputStream(os,8192);
            OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw,8192);

            while(generator.hasMoreString()) {
                String content = generator.generateLine();
                if (Objects.equals(content,"")) {
                    break;
                }
            }

        }

        bw.flush();
        bw.close();

        return HttpStreamGenerator.empty();
    }

    private boolean isNotAllowedExtension(Properties properties, String targetExtension) {
        for(String extension : properties.getProperty("not_allowed_extension").split(",")) {
            if (Objects.equals(extension,targetExtension)) {
                return true;
            }
        }

        return false;
    }
}
