package org.mmmr.services.swing.common;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateTableCellRenderer extends DefaultTableCellRenderer.UIResource {
    public enum Type {
        DATE, TIME, DATE_TIME;
    }

    private static final long serialVersionUID = -8217402048878663776L;

    protected DateFormat formatter;

    protected Type type;

    public DateTableCellRenderer() {
        this(Type.DATE_TIME);
    }

    public DateTableCellRenderer(Type type) {
        this.type = type;
        this.newFormatter();
    }

    /**
     * 
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    protected void newFormatter() {
        switch (this.type) {
            case DATE:
                this.formatter = DateFormat.getDateInstance(DateFormat.LONG, this.getLocale());
                break;
            case DATE_TIME:
                this.formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, this.getLocale());
                break;
            case TIME:
                this.formatter = DateFormat.getTimeInstance(DateFormat.LONG, this.getLocale());
                break;
        }
    }

    /**
     * 
     * @see java.awt.Component#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale l) {
        super.setLocale(l);
        this.newFormatter();
    }

    /**
     * 
     * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value) {
        this.setText((value == null) ? "" : this.formatter.format(value));
    }

    // @Override
    // public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    // if (value != null) {
    // value = Config.DATE_FORMAT.format(Date.class.cast(value));
    // }
    // return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    // }
}
