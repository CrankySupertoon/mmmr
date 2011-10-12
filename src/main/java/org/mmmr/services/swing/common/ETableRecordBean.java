package org.mmmr.services.swing.common;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * @author jdlandsh
 */
public class ETableRecordBean<T> implements ETableRecord<T> {
    protected T object;

    private List<String> orderedFields;

    private final Map<String, Object> originalValues = new HashMap<String, Object>();

    /**
     * 
     * Instantieer een nieuwe ETableRecordBean
     * 
     * @param orderedFields
     * @param o
     */
    public ETableRecordBean(List<String> orderedFields, T o) {
        this.object = o;
        this.orderedFields = orderedFields;
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#get(int)
     */
    @Override
    public Object get(int column) {
        try {
            Object property = PropertyUtils.getProperty(this.object, this.orderedFields.get(column));
            return property;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#getBean()
     */
    @Override
    public T getBean() {
        return this.object;
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

    public boolean hasChanged(String property) {
        try {
            Object ov = this.originalValues.get(property);
            return (ov != null) && !new EqualsBuilder().append(ov, PropertyUtils.getProperty(this.object, property)).isEquals();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#set(int, java.lang.Object)
     */
    @Override
    public void set(int column, Object newValue) {
        try {
            String property = this.orderedFields.get(column);
            if (this.originalValues.get(property) == null) {
                Object ov = PropertyUtils.getProperty(this.object, property);
                this.originalValues.put(property, ov == null ? Void.TYPE : ov);
            }
            PropertyUtils.setProperty(this.object, property, newValue);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @see org.mmmr.services.swing.common.ETableRecord#size()
     */
    @Override
    public int size() {
        return this.orderedFields.size();
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.valueOf(this.object);
    }
}
