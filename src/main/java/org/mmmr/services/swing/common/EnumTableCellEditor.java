package org.mmmr.services.swing.common;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class EnumTableCellEditor<T extends Enum<T>> extends DefaultCellEditor {
    private static final long serialVersionUID = 5169127745067354714L;

    private static <T extends Enum<T>> Vector<T> options(Class<T> enumType) {
        Vector<T> options = new Vector<T>();
        for (T option : EnumSet.allOf(enumType)) {
            options.add(option);
        }
        return options;
    }

    protected Class<T> enumType;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EnumTableCellEditor(Class<T> enumType) {
        super(new JComboBox(EnumTableCellEditor.options(enumType)));
        this.enumType = enumType;
    }

    public Locale getLocale() {
        return this.getComponent().getLocale();
    }

    public void setLocale(Locale l) {
        this.getComponent().setLocale(l);
    }
}