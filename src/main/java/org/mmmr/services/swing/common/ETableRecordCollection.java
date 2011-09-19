package org.mmmr.services.swing.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        return this.collection.get(column);
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.collection;
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
        this.collection.set(column, newValue);
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.collection.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.collection.toString();
    }
}
