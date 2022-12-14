package com.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.http.Bootstrap;
import com.http.PreProcessorComposite;
import com.http.request.HttpRequestMethod;
import com.http.request.HttpRequestPath;
import com.http.request.handler.HttpRequestHandlers;
import com.preprocessor.LoginPreProcessor;
import com.request.handler.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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

        HttpRequestHandlers handlers = HttpRequestHandlers.empty()
            .register(HttpRequestPath.of("/oauth/google/signUp"), HttpRequestMethod.GET, new OauthCodeRequest())
            .register(HttpRequestPath.of("/oauth/google/login"), HttpRequestMethod.GET, new OauthCodeRequest())
            .register(HttpRequestPath.of("/oauth/google/signUp/callback"), HttpRequestMethod.GET, new OauthUserCreator())
            .register(HttpRequestPath.of("/oauth/google/login/callback"), HttpRequestMethod.GET, new OauthUserFinder())

            .register(HttpRequestPath.of("/signUp"), HttpRequestMethod.POST, new HttpRequestUserCreator())
            .register(HttpRequestPath.of("/login"), HttpRequestMethod.POST, new HttpRequestUserFinder())
            .register(HttpRequestPath.of("/logout"), HttpRequestMethod.POST, new HttpSessionCloser())
            .register(HttpRequestPath.of("/upload"), HttpRequestMethod.POST, new HttpRequestFileUploader())
            .register(HttpRequestPath.of("/download"), HttpRequestMethod.POST, new HttpRequestFileDownloader())

            .register(HttpRequestPath.of("/page/signUp"), HttpRequestMethod.GET, new HttpRequestPageStream())
            .register(HttpRequestPath.of("/page/login"), HttpRequestMethod.GET, new HttpRequestPageStream())
            .register(HttpRequestPath.of("/page/upload"), HttpRequestMethod.GET, new HttpRequestPageStream())
            .register(HttpRequestPath.of("/page/download"), HttpRequestMethod.GET, new FileDownloadPageStream())

            .register(HttpRequestPath.ofResourcePath(), HttpRequestMethod.GET, new HttpResourceStream());

        Bootstrap bootstrap = new Bootstrap(preProcessors,handlers);
        bootstrap.start();
    }

}
