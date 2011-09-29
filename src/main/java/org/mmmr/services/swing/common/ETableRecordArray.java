package org.mmmr.services.swing.common;

import java.util.Arrays;

/**
 * @author jdlandsh
 */
public class ETableRecordArray implements ETableRecord {
    protected Object[] array;

    /**
     * 
     * Instantieer een nieuwe ETableRecordArray
     * 
     * @param o
     */
    public ETableRecordArray(Object... o) {
        this.array = o;
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        return this.array[column];
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.array;
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#getStringValue(int)
     */
    @Override
    public String getStringValue(int column) {
        Object value = this.get(column);
        return value == null ? null : "" + value;
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#set(int, java.lang.Object)
     */
    @Override
    public void set(int column, Object newValue) {
        this.array[column] = newValue;
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.array.length;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Arrays.toString(this.array);
    }
}
