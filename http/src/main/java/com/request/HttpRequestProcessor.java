package com.request;

import com.HttpLengthLimiter;
import com.HttpResponseStatus;
import com.HttpsStream;
import com.StringStream;
import com.exception.*;
import com.header.HttpHeaders;
import com.releaser.FileResourceCloser;
import com.releaser.ResourceCloser;
import com.request.handler.HttpRequestHandler;
import com.request.handler.HttpRequestHandlers;
import com.template.FileTemplateReplacer;
import com.template.TemplateNodes;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestProcessor {

    public HttpsStream process(HttpsStream requestGenerator, HttpRequestHandlers handlers, HttpLengthLimiter requestHeadersLengthLimit, HttpLengthLimiter requestBodyLengthLimit) throws IOException {
        try {
            /**
             * 1. request 받기: 로직 모듈화 필요
               request 의 구성요소를 하나로 묶기 위해 Request 클래스화 필요 - method, path, header, body, values 를 멤버변수로
                1-1. StartLine 가공
                    - path 는 enum 으로 처리하여 path 에 해당되는 처리클래스와 바인딩
                    - method 는 enum 으로 처리하여 메서드에 해당하는 처리클래스와 바인딩 (각 메서드 처리 클래스 필요)
                    - GET 인 경우 values 가공 필요 (Values 클래스화 필요)
            **/
            if (!requestGenerator.hasMoreString()) {
                System.out.println("favicon1");
                throw new FaviconException();
            }

            String startLineText = requestGenerator.generateLine();
            System.out.println(startLineText);

            Optional<HttpRequestStartLine> startLineParser = Optional.of(HttpRequestStartLine.parse(startLineText));
            HttpRequestStartLine httpRequestStartLine = startLineParser.get();

            /**
                1-2. Header 가공
                    - Headers 클래스화 필요 - List<Filed> 를 멤버변수로
                    - Field 는 String 으로 받으므로 클래스화 필요 - filed-name(대소문자구별 x), field-value 를 멤버변수로
                    - HTTP Header 에 길이 제한 스펙은 없지만 자체적으로 8192로 길이 제한
                    - 8192보다 긴 경우 431 Request header too large 응답
            **/
            HttpHeaders httpHeaders = HttpHeaders.parse(requestGenerator,requestHeadersLengthLimit);

            /**
                1-3. Body 가공
                    - Body 는 String 으로 받으므로 클래스화 필요
                    - GET,PATCH 외 메서드들은 Body 가공 필요
                    - HTTP Body 에 길이 제한 스펙은 없지만 자체적으로 Header 의 Content-Length 값을 2,097,152(2MB)로 길이 제한
                    - 2MB 이상이면 413 Request Entity Too Large 응답
                    - values 이면 Values 클래스 가공 필요
           **/

            /**
             * 2. response 보내기: 로직 모듈화 필요
                2-1. request 처리를 통해 response 메시지 각 구성요소(StartLine,Header,Body) 얻기
                    - Request 객체를 받아서 각 구성요소를 가공하는 클래스 필요
                    - Status Code 와 Status Message 는 enum 으로 처리
                    - body 내용이 길어지는 경우 메모리 초과될 수 있으므로 스트림 처리 필요
                    - body 길이는 2,097,152(2MB)로 길이 제한
            **/

            HttpRequestPath path = httpRequestStartLine.getPath();
            HttpRequestMethod method = httpRequestStartLine.getMethod();

            if (Objects.equals(path,HttpRequestPath.of("/favicon.ico"))) {
                System.out.println("favicon2");
                throw new FaviconException();
            }

            HttpRequestHandler httpRequestHandler = handlers.find(path, method);
            HttpsStream responseBody = httpRequestHandler.handle(httpHeaders, requestGenerator, requestBodyLengthLimit);

            String startLine = createHttpResponseStartLine(HttpResponseStatus.CODE_200);
            InputStream startLineOutput = new ByteArrayInputStream(startLine.getBytes(StandardCharsets.UTF_8));
            StringStream startLineGenerator = StringStream.of(startLineOutput);
            HttpsStream responseStartLine = HttpsStream.of(startLineGenerator);

            return responseStartLine.sequenceOf(responseBody);

        } catch (Exception e) {
            HttpResponseStatus status = HttpResponseStatus.httpResponseStatusOf(e);
            return createHttpErrorResponse(status);
        }
    }

    private HttpsStream createHttpErrorResponse(HttpResponseStatus status) throws IOException {
        String startLine = createHttpResponseStartLine(status)+"\n";
        InputStream startLineInput = new ByteArrayInputStream(startLine.getBytes(StandardCharsets.UTF_8));
        StringStream startLineGenerator = StringStream.of(startLineInput);
        HttpsStream responseStartLine = HttpsStream.of(startLineGenerator);

        TemplateNodes templateNodes = new TemplateNodes();
        templateNodes.register("statusCode",status.code());
        templateNodes.register("statusMsg",status.message());

        Path errorTemplateFile = Path.of("src","main","resources", "template","error.html");
        Path replacedFile = Path.of("src","main","resources", "template","errorBody.html");

        String startTemplate = "{{";
        String endTemplate = "}}";

        try (FileTemplateReplacer replacer = FileTemplateReplacer.of(errorTemplateFile,replacedFile);
        ) {
            replacer.replace(templateNodes, startTemplate, endTemplate);
        }

        InputStream bodyInput = new FileInputStream(replacedFile.toString());
        StringStream bodyGenerator = StringStream.of(bodyInput);
        HttpsStream responseBody = HttpsStream.of(bodyGenerator);

        ResourceCloser releaser = new FileResourceCloser(Path.of("src","main","resources", "template","errorBody.html").toFile());
        responseBody.registerReleaser(releaser);

        return responseStartLine.sequenceOf(responseBody);
    }

    public String createHttpResponseStartLine(HttpResponseStatus status) {
        StringBuilder startLine = new StringBuilder();
        String statusCode = status.code();
        String statusMsg = status.message();

        System.out.println(statusCode + " " + statusMsg);

        startLine.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMsg).append("\n")
                .append("Content-Type: text/html;charset=UTF-8\n");

        return startLine.toString();

    }
}
