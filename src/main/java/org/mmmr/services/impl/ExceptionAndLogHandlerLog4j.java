package org.mmmr.services.impl;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;
import org.mmmr.services.Config;
import org.mmmr.services.IOMethods;
import org.mmmr.services.interfaces.ExceptionAndLogHandlerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * log4j over slf4j logging and exception handling<br>
 * download and sourcecode available from ... (see links)
 * 
 * @author Jurgen
 * 
 * @see http://logging.apache.org/log4j/1.2/
 * @see http://www.slf4j.org/
 */
public class ExceptionAndLogHandlerLog4j implements ExceptionAndLogHandlerI {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAndLogHandlerLog4j.class);

    @Override
    public void adjustLogging(Config cfg) throws IOException {
        Level level;
        String levelstr = cfg.getProperty("logging.level", "?");
        if (!"?".equals(levelstr)) {
            if ("TRACE".equals(levelstr)) {
                level = Level.TRACE;
            } else if ("DEBUG".equals(levelstr)) {
                level = Level.DEBUG;
            } else if ("INFO".equals(levelstr)) {
                level = Level.INFO;
            } else if ("WARN".equals(levelstr)) {
                level = Level.WARN;
            } else if ("ERROR".equals(levelstr)) {
                level = Level.ERROR;
            } else if ("FATAL".equals(levelstr)) {
                level = Level.FATAL;
            } else {
                level = Level.OFF;
            }
        } else {
            level = Level.INFO;
        }
        org.apache.log4j.Logger.getRootLogger().setLevel(level);
        Enumeration<?> allAppenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
        while (allAppenders.hasMoreElements()) {
            Appender appender = Appender.class.cast(allAppenders.nextElement());
            LevelRangeFilter filter = LevelRangeFilter.class.cast(appender.getFilter());
            filter.setLevelMin(level);
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#handle(org.mmmr.services.Config, java.lang.String, java.lang.String,
     *      java.lang.Exception)
     */
    @Override
    public void handle(Config cfg, String title, String message, Exception ex) {
        this.log(ex);
        IOMethods.showWarning(cfg, title, message);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Exception)
     */
    @Override
    public void log(Exception ex) {
        ExceptionAndLogHandlerLog4j.logger.error("exception", ex);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Object)
     */
    @Override
    public void log(Object object) {
        ExceptionAndLogHandlerLog4j.logger.info("{}", object);
    }
}
