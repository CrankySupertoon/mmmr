package org.mmmr.services.swing.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6723524
 */
public class BooleanTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 2577869717107398445L;

    private JCheckBox renderer;

    public BooleanTableCellRenderer() {
        this.renderer = new JCheckBox();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Boolean b = (Boolean) value;
        if (b != null) {
            this.renderer.setSelected(b);
        } else {
            this.renderer.setSelected(false);
        }
        if (isSelected) {
            this.renderer.setForeground(table.getSelectionForeground());
            this.renderer.setBackground(table.getSelectionBackground());
        } else {
            Color bg = this.getBackground();
            this.renderer.setForeground(this.getForeground());
            // We have to create a new color object because Nimbus returns
            // a color of type DerivedColor, which behaves strange, not sure
            // why.
            this.renderer.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
            this.renderer.setOpaque(true);
        }
        return this.renderer;
    }
}