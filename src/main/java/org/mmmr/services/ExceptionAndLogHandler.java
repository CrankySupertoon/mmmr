package org.mmmr.services;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionAndLogHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAndLogHandler.class);

    public static void handle(Config cfg, String title, String message, Exception ex) {
	ExceptionAndLogHandler.log(ex);
	JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), message, title, JOptionPane.ERROR_MESSAGE, cfg.getIcon());
    }

    public static void log(Exception ex) {
	ExceptionAndLogHandler.logger.error("exception", ex);
    }

    public static void log(Object object) {
	ExceptionAndLogHandler.logger.debug("{}", object);
    }
}
