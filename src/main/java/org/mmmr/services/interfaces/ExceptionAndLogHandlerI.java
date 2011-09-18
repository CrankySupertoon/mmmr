package org.mmmr.services.interfaces;

import java.io.IOException;

import org.mmmr.services.Config;

/**
 * @author Jurgen
 */
public interface ExceptionAndLogHandlerI {
    public abstract void adjustLogging(Config cfg) throws IOException;

    public abstract void handle(Config cfg, String title, String message, Exception ex);

    public abstract void log(Exception ex);

    public abstract void log(Object object);
}
