package org.mmmr.services.swing.common;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

// javax.swing.text.NumberFormatter
public class NumberTableCellEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 5169127745067354714L;

    protected NumberFormat formatter;

    public NumberTableCellEditor() {
        super(new JTextField());

        final JTextField jtf = JTextField.class.cast(this.getComponent());
        jtf.setBorder(null);
        jtf.removeActionListener(this.delegate);
        this.delegate = new EditorDelegate() {
            private static final long serialVersionUID = 6553117639786915624L;

            @Override
            public Object getCellEditorValue() {
                if (jtf.getText().equals("")) {
                    return null;
                }
                try {
                    return NumberTableCellEditor.this.formatter.parseObject(jtf.getText());
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }

            /**
             * 
             * @see javax.swing.DefaultCellEditor.EditorDelegate#setValue(java.lang.Object)
             */
            @Override
            public void setValue(Object value) {
                jtf.setText((value != null) ? NumberTableCellEditor.this.formatter.format(value) : "");
            }
        };
        jtf.addActionListener(this.delegate);

        this.newFormatter();
    }

    public Locale getLocale() {
        return this.getComponent().getLocale();
    }

    protected void newFormatter() {
        this.formatter = NumberFormat.getInstance(this.getLocale());
    }

    public void setLocale(Locale l) {
        this.getComponent().setLocale(l);
        this.newFormatter();
    }
}