package org.mmmr.services;

public class ExceptionAndLogHandler {
    private static ExceptionAndLogHandlerI exceptionAndLogHandler = new ExceptionAndLogHandlerSimple();

    private static ExceptionAndLogHandlerI getExceptionAndLogHandler() {
        if (ExceptionAndLogHandler.exceptionAndLogHandler instanceof ExceptionAndLogHandlerSimple) {
            try {
                ExceptionAndLogHandler.exceptionAndLogHandler = (ExceptionAndLogHandlerI) Class.forName(
                        "org.mmmr.services.ExceptionAndLogHandlerLog4j").newInstance();
            } catch (Exception ex) {
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
