package com.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.db.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.http.Bootstrap;
import com.http.PreProcessorComposite;
import com.http.request.HttpRequestMethod;
import com.http.request.HttpRequestPath;
import com.preprocessor.LoginPreProcessor;
import com.request.handler.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    private static final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Logger LOG = lc.getLogger(com.main.Main.class);
    private static final List<String> loggers = List.of("com.main.Main",
            "com.http.Bootstrap",
            "com.http.request.HttpRequestProcessor",
            "com.http.header.HttpHeaders",
            "com.http.request.handler.HttpRequestFileUploader",
            "com.http.exception.EmptyRequestException"
    );

    private static void setLogMode(boolean logMode) {
        if (logMode) {
            changeLoggerLevel(Level.DEBUG);
            return;
        }
        changeLoggerLevel(Level.INFO);
    }

    private static void changeLoggerLevel(Level level) {
        for(String changeLogger : loggers) {
            Logger logger = lc.getLogger(changeLogger);
            logger.setLevel(level);
        }
    }

    public static void main(String[] args) throws IOException {
        setLogMode(true);

        LoginPreProcessor loginPreprocessor = LoginPreProcessor.allPathLoginPreProcessor()
                .filtered(HttpRequestPath.of("/signUp"))
                .filtered(HttpRequestPath.of("/page/signUp"))
                .filtered(HttpRequestPath.of("/login"))
                .filtered(HttpRequestPath.of("/oauth/google/signUp"))
                .filtered(HttpRequestPath.of("/oauth/google/login"))
                .filtered(HttpRequestPath.of("/oauth/google/signUp/callback"))
                .filtered(HttpRequestPath.of("/oauth/google/login/callback"))
                .filtered(HttpRequestPath.of("/page/login"))
                .filtered(HttpRequestPath.of("/favicon.ico"));

        PreProcessorComposite preProcessors = PreProcessorComposite.empty()
                .sequenceOf(loginPreprocessor);

        Bootstrap bootstrap = new Bootstrap(preProcessors);

        bootstrap.registerHandler(HttpRequestPath.of("/oauth/google/signUp"), HttpRequestMethod.GET, new OauthCodeRequest());
        bootstrap.registerHandler(HttpRequestPath.of("/oauth/google/login"), HttpRequestMethod.GET, new OauthCodeRequest());
        bootstrap.registerHandler(HttpRequestPath.of("/oauth/google/signUp/callback"), HttpRequestMethod.GET, new OauthUserCreator());
        bootstrap.registerHandler(HttpRequestPath.of("/oauth/google/login/callback"), HttpRequestMethod.GET, new OauthUserFinder());

        bootstrap.registerHandler(HttpRequestPath.of("/signUp"), HttpRequestMethod.POST, new HttpRequestUserCreator());
        bootstrap.registerHandler(HttpRequestPath.of("/login"), HttpRequestMethod.POST, new HttpRequestUserFinder());
        bootstrap.registerHandler(HttpRequestPath.of("/logout"), HttpRequestMethod.POST, new HttpSessionCloser());
        bootstrap.registerHandler(HttpRequestPath.of("/upload"), HttpRequestMethod.POST, new HttpRequestFileUploader());
        bootstrap.registerHandler(HttpRequestPath.of("/download"), HttpRequestMethod.POST, new HttpRequestFileDownloader());

        bootstrap.registerHandler(HttpRequestPath.of("/page/signUp"), HttpRequestMethod.GET, new HttpRequestPageStream());
        bootstrap.registerHandler(HttpRequestPath.of("/page/login"), HttpRequestMethod.GET, new HttpRequestPageStream());
        bootstrap.registerHandler(HttpRequestPath.of("/page/upload"), HttpRequestMethod.GET, new HttpRequestPageStream());
        bootstrap.registerHandler(HttpRequestPath.of("/page/download"), HttpRequestMethod.GET, new FileDownloadPageStream());

        bootstrap.registerHandler(HttpRequestPath.ofResourcePath(), HttpRequestMethod.GET, new HttpResourceStream());

        bootstrap.start();
    }

}
