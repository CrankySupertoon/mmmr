package org.mmmr.services;

public interface ExceptionAndLogHandlerI {

    public abstract void handle(Config cfg, String title, String message, Exception ex);

    public abstract void log(Exception ex);

    public abstract void log(Object object);

}
