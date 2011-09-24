package org.mmmr.services.swing.common;

/**
 * @author jdlandsh
 */
public interface ETableRecord<T> {
    public abstract Object get(int column);

    public abstract T getBean();

    public abstract String getStringValue(int column);

    public abstract void set(int column, Object newValue);

    public abstract int size();
}
