package org.mmmr.services.swing.common;

import java.util.List;

public class ETableRecordFields implements ETableRecord {
    protected _ObjectWrapper wrapper;

    protected Object object;

    private List<String> orderedFields;

    /**
     * 
     * Instantieer een nieuwe ETableRecordBean
     * 
     * @param orderedFields
     * @param o
     */
    public ETableRecordFields(List<String> orderedFields, Object o) {
        this.object = o;
        this.orderedFields = orderedFields;
        this.wrapper = new _ObjectWrapper(o);
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        try {
            return this.wrapper.get(this.orderedFields.get(column));
        } catch (_ObjectWrapper.FieldNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#getBean()
     */
    @Override
    public Object getBean() {
        return this.object;
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
        this.wrapper.set(this.orderedFields.get(column), newValue);
    }

    /**
     * 
     * @see be.ugent.oasis.tools.hqlbuilder.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.orderedFields.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "" + this.object;
    }
}
