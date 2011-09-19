package org.mmmr.services.swing.common;

import java.util.Arrays;

public class ETableRecordArray implements ETableRecord {
    protected Object[] array;

    /**
     * 
     * Instantieer een nieuwe ETableRecordArray
     * 
     * @param o
     */
    public ETableRecordArray(Object o) {
        this.array = (Object[]) o;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        return this.array[column];
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.array;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getStringValue(int)
     */
    @Override
    public String getStringValue(int column) {
        Object value = this.get(column);
        return value == null ? null : "" + value;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#set(int, java.lang.Object)
     */
    @Override
    public void set(int column, Object newValue) {
        this.array[column] = newValue;
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.array.length;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Arrays.toString(this.array);
    }
}
