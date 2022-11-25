package com.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.http.Bootstrap;
import com.http.PreProcessorComposite;
import com.preprocessor.LoginPreProcessor;
import com.http.request.HttpRequestMethod;
import com.http.request.HttpRequestPath;
import com.request.handler.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Logger LOG = lc.getLogger(com.main.Main.class);
    private static final List<String> loggers = List.of("com.main.Main",
            "com.Bootstrap",
            "com.request.HttpRequestProcessor",
            "com.header.HttpHeaders",
            "com.request.handler.HttpRequestFileUploader"
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
                .filtered(HttpRequestPath.of("/page/login"))
                .filtered(HttpRequestPath.of("/favicon.ico"));

        PreProcessorComposite preProcessors = PreProcessorComposite.empty()
                .sequenceOf(loginPreprocessor);

        Bootstrap bootstrap = new Bootstrap(preProcessors);

        bootstrap.registerHandler(HttpRequestPath.of("/signUp"), HttpRequestMethod.POST, new HttpRequestUserCreator());
        bootstrap.registerHandler(HttpRequestPath.of("/login"), HttpRequestMethod.POST, new HttpRequestUserFinder());
        bootstrap.registerHandler(HttpRequestPath.of("/upload"), HttpRequestMethod.POST, new HttpRequestFileUploader());
        bootstrap.registerHandler(HttpRequestPath.of("/download"), HttpRequestMethod.POST, new HttpRequestFileDownloader());
        bootstrap.registerHandler(HttpRequestPath.of("/page/login"), HttpRequestMethod.GET, new HttpRequestPageStream());
        bootstrap.registerHandler(HttpRequestPath.of("/page/upload"), HttpRequestMethod.GET, new HttpRequestPageStream());
        bootstrap.registerHandler(HttpRequestPath.of("/page/download"), HttpRequestMethod.GET, new FileDownloadPageStream());
        bootstrap.registerHandler(HttpRequestPath.ofResourcePath(), HttpRequestMethod.GET, new HttpResourceStream());

        bootstrap.start();
    }

}
