package org.mmmr.services;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jurgen
 */
public class ExceptionAndLogHandlerLog4j implements ExceptionAndLogHandlerI {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAndLogHandlerLog4j.class);

    @Override
    public void handle(Config cfg, String title, String message, Exception ex) {
        this.log(ex);
        JOptionPane.showMessageDialog(FancySwing.getCurrentFrame(), message, title, JOptionPane.ERROR_MESSAGE, cfg.getIcon());
    }

    @Override
    public void log(Exception ex) {
        ExceptionAndLogHandlerLog4j.logger.error("exception", ex);
    }

    @Override
    public void log(Object object) {
        ExceptionAndLogHandlerLog4j.logger.info("{}", object);
    }
}
