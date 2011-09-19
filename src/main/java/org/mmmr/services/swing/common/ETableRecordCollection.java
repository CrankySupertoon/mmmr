package org.mmmr.services.swing.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jdlandsh
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ETableRecordCollection implements ETableRecord {
    protected List collection;

    public ETableRecordCollection() {
        this.collection = new ArrayList();
    }

    public ETableRecordCollection(Collection o) {
        this.collection = new ArrayList(o);
    }

    public ETableRecordCollection(List o) {
        this.collection = List.class.cast(o);
    }

    public void add(Object item) {
        this.collection.add(item);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        return this.collection.get(column);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.collection;
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
        this.collection.set(column, newValue);
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.collection.size();
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.collection.toString();
    }
}
