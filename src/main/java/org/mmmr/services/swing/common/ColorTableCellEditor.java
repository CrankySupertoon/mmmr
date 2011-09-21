package org.mmmr.services.swing.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /** serialVersionUID */
    private static final long serialVersionUID = 819809458892249679L;

    protected Locale locale;

    Color currentColor;

    JButton button;

    JColorChooser colorChooser;

    JDialog dialog;

    protected static final String EDIT = "edit";

    public ColorTableCellEditor() {
        // Set up the editor (from the table's point of view),
        // which is a button.
        // This button brings up the color chooser dialog,
        // which is the editor from the user's point of view.
        this.button = new JButton();
        this.button.setActionCommand(ColorTableCellEditor.EDIT);
        this.button.addActionListener(this);
        this.button.setBorderPainted(false);

        // Set up the dialog that the button brings up.
        this.colorChooser = new JColorChooser();
        this.dialog = JColorChooser.createDialog(this.button, "Pick a Color", true, // modal
                this.colorChooser, this, // OK button handler
                null); // no CANCEL button handler

    }

    /**
     * Handles events from the editor button and from the dialog's OK button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (ColorTableCellEditor.EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so
            // bring up the dialog.
            this.button.setBackground(this.currentColor);
            this.colorChooser.setColor(this.currentColor);
            this.dialog.setVisible(true);

            // Make the renderer reappear.
            this.fireEditingStopped();

        } else { // User pressed dialog's "OK" button.
            this.currentColor = this.colorChooser.getColor();
        }
    }

    /**
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        return this.currentColor;
    }

    /**
     * 
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentColor = (Color) value;
        return this.button;
    }

    public void setLocale(Locale l) {
        this.locale = l;
    }
}
