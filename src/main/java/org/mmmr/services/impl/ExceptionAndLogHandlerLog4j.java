package org.mmmr.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.varia.LevelRangeFilter;
import org.mmmr.services.Config;
import org.mmmr.services.UtilityMethods;
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
    private static final Logger logger = LoggerFactory.getLogger("mmmr");

    public static void noFileLogging() throws IOException {
        RootLogger rootLogger = (RootLogger) org.apache.log4j.Logger.getRootLogger();
        rootLogger.removeAppender("ROLLINGFILE");
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#adjustLogging(org.mmmr.services.Config)
     */
    @Override
    public void adjustLogging(Config cfg) throws IOException {
        Level level;
        String levelstr = cfg.getProperty("logging.level", "?"); //$NON-NLS-1$ //$NON-NLS-2$
        if (!"?".equals(levelstr)) { //$NON-NLS-1$
            if ("TRACE".equals(levelstr)) { //$NON-NLS-1$
                level = Level.TRACE;
            } else if ("DEBUG".equals(levelstr)) { //$NON-NLS-1$
                level = Level.DEBUG;
            } else if ("INFO".equals(levelstr)) { //$NON-NLS-1$
                level = Level.INFO;
            } else if ("WARN".equals(levelstr)) { //$NON-NLS-1$
                level = Level.WARN;
            } else if ("ERROR".equals(levelstr)) { //$NON-NLS-1$
                level = Level.ERROR;
            } else if ("FATAL".equals(levelstr)) { //$NON-NLS-1$
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos);
        System.getProperties().list(out);
        out.flush();
        out.close();
        this.log(new String(baos.toByteArray()));
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#handle(org.mmmr.services.Config, java.lang.String, java.lang.String,
     *      java.lang.Exception)
     */
    @Override
    public void handle(Config cfg, String title, String message, Exception ex) {
        this.log(ex);
        UtilityMethods.showWarning(cfg, title, message);
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Exception)
     */
    @Override
    public void log(Exception ex) {
        ExceptionAndLogHandlerLog4j.logger.error("exception", ex); //$NON-NLS-1$
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.ExceptionAndLogHandlerI#log(java.lang.Object)
     */
    @Override
    public void log(Object object) {
        ExceptionAndLogHandlerLog4j.logger.info("{}", object); //$NON-NLS-1$
    }
}
