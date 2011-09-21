package org.mmmr.services.swing.common;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.table.DefaultTableCellRenderer;

// javax.swing.text.NumberFormatter
public class NumberTableCellRenderer extends DefaultTableCellRenderer.UIResource {
    private static final long serialVersionUID = 5169127745067354714L;

    protected NumberFormat formatter;

    public NumberTableCellRenderer() {
        this.newFormatter();
    }

    protected void newFormatter() {
        this.formatter = NumberFormat.getInstance(this.getLocale());
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
    protected void setValue(Object value) {
        this.setText((value == null) ? "" : this.formatter.format(value));
    }
}