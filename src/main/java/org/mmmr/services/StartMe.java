package org.mmmr.services;

public interface StartMe {

    public abstract void setCfg(Config cfg);

    public abstract void setStatusFrame(StatusFrame statusFrame);

    public abstract void start(String[] args) throws Exception;
}