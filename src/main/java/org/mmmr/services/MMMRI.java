package org.mmmr.services;

import org.mmmr.services.swing.StatusWindow;

/**
 * interface needed because libraries (jars) are not loaded yet and MMR uses external classes
 * 
 * @author Jurgen
 */
public interface MMMRI {
    public abstract void setCfg(Config cfg);

    public abstract void setStatusWindow(StatusWindow statusWindow);

    public abstract void start(String[] args) throws Exception;
}