package org.mmmr.services.swing.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class DateTableCellEditor extends DefaultCellEditor {
    public enum Type {
        DATE, TIME, DATE_TIME;
    }

    private static final long serialVersionUID = 5169127745067354714L;

    protected DateFormat formatter;

    protected Type type;

    public DateTableCellEditor() {
        this(Type.DATE_TIME);
    }

    public DateTableCellEditor(Type type) {
        super(new JTextField());

        final JTextField jtf = JTextField.class.cast(this.getComponent());
        jtf.removeActionListener(this.delegate);
        this.delegate = new EditorDelegate() {
            private static final long serialVersionUID = 6553117639786915624L;

            /**
             * 
             * @see javax.swing.DefaultCellEditor.EditorDelegate#getCellEditorValue()
             */
            @Override
            public Object getCellEditorValue() {
                if (jtf.getText().equals("")) {
                    return null;
                }
                try {
                    return DateTableCellEditor.this.formatter.parseObject(jtf.getText());
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
                jtf.setText((value != null) ? DateTableCellEditor.this.formatter.format(value) : "");
            }
        };
        jtf.addActionListener(this.delegate);

        this.type = type;
        this.newFormatter();
    }

    public Locale getLocale() {
        return this.getComponent().getLocale();
    }

    protected void newFormatter() {
        switch (this.type) {
            case DATE:
                this.formatter = DateFormat.getDateInstance(DateFormat.SHORT, this.getLocale());
                break;
            case DATE_TIME:
                this.formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, this.getLocale());
                break;
            case TIME:
                this.formatter = DateFormat.getTimeInstance(DateFormat.SHORT, this.getLocale());
                break;
        }
    }

    public void setLocale(Locale l) {
        this.getComponent().setLocale(l);
    }
}