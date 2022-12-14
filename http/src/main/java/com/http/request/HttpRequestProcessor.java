package com.http.request;

import com.http.*;
import com.http.closer.FileResourceCloser;
import com.http.closer.ResourceCloser;
import com.http.exception.InputNullParameterException;
import com.http.header.HttpHeaders;
import com.http.request.handler.HttpRequestHandler;
import com.http.request.handler.HttpRequestHandlers;
import com.http.response.HttpResponseStatus;
import com.http.response.HttpResponseStream;
import com.http.template.FileTemplateReplacer;
import com.http.template.TemplateFileStream;
import com.http.template.TemplateNodes;
import com.http.template.TemplateText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

public class HttpRequestProcessor implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestProcessor.class);
    private static final ResourceFinder RESOURCE_FINDER = new ResourceFinder();
    private static final TemplateFileStream ERROR_TEMPLATE_FILE = RESOURCE_FINDER.findTemplate("error.html");
    private static final File REPLACED_FILE = Path.of("src","main","resources", "errorBody.html").toFile();
    private final RetryHttpRequestStream requestStream;
    private final HttpRequestHandlers handlers;
    private final PreProcessor preprocessor;
    private final HttpRequestLengthLimiters requestLengthLimiters;

    private HttpRequestProcessor(RetryHttpRequestStream requestStream, HttpRequestHandlers handlers, PreProcessor preprocessor, HttpRequestLengthLimiters requestLengthLimiters) {
        if (requestStream == null || handlers == null || preprocessor == null || requestLengthLimiters == null) {
            throw new InputNullParameterException(
                    "requestStream: "+requestStream+"\n"+
                    "handlers: "+handlers+"\n"+
                    "preprocessor: "+preprocessor+"\n"+
                    "requestLengthLimiters: "+requestLengthLimiters+"\n"
            );
        }

        this.requestStream = requestStream;
        this.handlers = handlers;
        this.preprocessor = preprocessor;
        this.requestLengthLimiters = requestLengthLimiters;
    }

    public static HttpRequestProcessor from(RetryHttpRequestStream requestStream, HttpRequestHandlers handlers, PreProcessor preprocessor, HttpRequestLengthLimiters requestLengthLimiters) {
        return new HttpRequestProcessor(requestStream,handlers, preprocessor, requestLengthLimiters);
    }

    public HttpResponseStream process() {
        try {
            /**
             * 1. request 받기: 로직 모듈화 필요
               request 의 구성요소를 하나로 묶기 위해 Request 클래스화 필요 - method, path, header, body, values 를 멤버변수로
                1-1. StartLine 가공
                    - path 는 enum 으로 처리하여 path 에 해당되는 처리클래스와 바인딩
                    - method 는 enum 으로 처리하여 메서드에 해당하는 처리클래스와 바인딩 (각 메서드 처리 클래스 필요)
                    - GET 인 경우 values 가공 필요 (Values 클래스화 필요)
            **/
            LOG.debug("<HTTP Request Start Line>");
            String startLineText = requestStream.generateLine();
            LOG.debug(startLineText);
            LOG.debug("<--HTTP Request Start Line-->");

            HttpRequestStartLine httpRequestStartLine = getStartLine(startLineText);

            /**
                1-2. Header 가공
                    - Headers 클래스화 필요 - List<Filed> 를 멤버변수로
                    - Field 는 String 으로 받으므로 클래스화 필요 - filed-name(대소문자구별 x), field-value 를 멤버변수로
                    - HTTP Header 에 길이 제한 스펙은 없지만 자체적으로 8192로 길이 제한
                    - 8192보다 긴 경우 431 Request header too large 응답
            **/
            LOG.debug("<HTTP Request Headers>");
            HttpHeaders httpHeaders = HttpHeaders.parse(requestStream,requestLengthLimiters);
            httpHeaders.display();
            LOG.debug("<--HTTP Request Headers-->");

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
            HttpRequestQueryString queryString = httpRequestStartLine.getQueryString();

            try {
                preprocessor.process(path, httpHeaders);
                LOG.debug("Preprocess Complete");
            } catch (Exception e) {
                HttpResponseStatus status = HttpResponseStatus.httpResponseStatusOf(e);
                return createHttpErrorResponse(status);
            }

            HttpRequestHandler httpRequestHandler = handlers.find(path, method);
            HttpResponseStream responseMsg = httpRequestHandler.handle(path, httpHeaders, requestStream, queryString, requestLengthLimiters);

            return responseMsg;
        } catch (Exception e) {
            HttpResponseStatus status = HttpResponseStatus.httpResponseStatusOf(e);
            return createHttpErrorResponse(status);
        }
    }

    private HttpRequestStartLine getStartLine(String startLineText) {
        Optional<HttpRequestStartLine> startLineParser = Optional.of(HttpRequestStartLine.parse(startLineText));
        return startLineParser.get();
    }

    private HttpResponseStream createHttpErrorResponse(HttpResponseStatus status) {
        TemplateNodes templateNodes = new TemplateNodes();
        templateNodes.register("statusCode",status.code());
        templateNodes.register("statusMsg",status.message());

        HttpMessageStream responseHeaders = HttpMessageStream.of("Content-Type: text/html;charset=UTF-8");

        try (FileTemplateReplacer replacer = FileTemplateReplacer.of(ERROR_TEMPLATE_FILE, REPLACED_FILE.toPath());
        ) {
            replacer.replace(templateNodes, TemplateText.ERROR_TEMPLATE);

            ResourceCloser releaser = new FileResourceCloser(REPLACED_FILE);

            InputStream bodyInput = new FileInputStream(REPLACED_FILE);
            HttpMessageStream responseBody = HttpMessageStream.of(bodyInput,releaser);

            return HttpResponseStream.from(
                    status,
                    responseHeaders,
                    responseBody
            );
        } catch (IOException e) {
            return createHttpErrorResponse(HttpResponseStatus.CODE_500);
        }
    }

    @Override
    public void close() {
        requestStream.close();
    }
}
