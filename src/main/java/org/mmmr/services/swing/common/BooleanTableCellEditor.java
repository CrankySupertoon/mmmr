package org.mmmr.services.swing.common;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class BooleanTableCellEditor extends DefaultCellEditor {
    /** serialVersionUID */
    private static final long serialVersionUID = -1148800983303008114L;

    public BooleanTableCellEditor() {
        super(new JCheckBox());
        JCheckBox checkBox = (JCheckBox) this.getComponent();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    }
}