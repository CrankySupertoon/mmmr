package org.mmmr.services.swing.common;

public interface ETableRecord {

    public abstract Object get(int column);

    public abstract Object getBean();

    public abstract String getStringValue(int column);

    public abstract void set(int column, Object newValue);

    public abstract int size();
}
