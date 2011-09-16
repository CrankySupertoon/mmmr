package org.mmmr.services;

import java.io.IOException;

import org.mmmr.services.impl.ExceptionAndLogHandlerSimple;
import org.mmmr.services.interfaces.ExceptionAndLogHandlerI;

public class ExceptionAndLogHandler {
    private static ExceptionAndLogHandlerI exceptionAndLogHandler = new ExceptionAndLogHandlerSimple();

    public static void adjustLogging(Config cfg) throws IOException {
        ExceptionAndLogHandler.exceptionAndLogHandler.adjustLogging(cfg);
    }

    private static ExceptionAndLogHandlerI getExceptionAndLogHandler() {
        if (ExceptionAndLogHandler.exceptionAndLogHandler instanceof ExceptionAndLogHandlerSimple) {
            try {
                ExceptionAndLogHandler.exceptionAndLogHandler = (ExceptionAndLogHandlerI) Class.forName(
                        "org.mmmr.services.impl.ExceptionAndLogHandlerLog4j").newInstance();
                ExceptionAndLogHandler.exceptionAndLogHandler.log("log4j over slf4j");
            } catch (Throwable ex) {
                //
            }
        }

        return ExceptionAndLogHandler.exceptionAndLogHandler;
    }

    public static void handle(Config cfg, String title, String message, Exception ex) {
        ExceptionAndLogHandler.getExceptionAndLogHandler().handle(cfg, title, message, ex);
    }

    public static void log(Exception ex) {
        ExceptionAndLogHandler.getExceptionAndLogHandler().log(ex);
    }

    public static void log(Object object) {
        ExceptionAndLogHandler.getExceptionAndLogHandler().log(object);
    }
}
