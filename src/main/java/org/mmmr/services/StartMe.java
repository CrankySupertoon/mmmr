package org.mmmr.services;

/**
 * @author Jurgen
 */
public interface StartMe {
    public abstract void setCfg(Config cfg);

    public abstract void setStatusWindow(StatusWindow statusWindow);

    public abstract void start(String[] args) throws Exception;
}