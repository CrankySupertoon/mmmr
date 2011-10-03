package org.mmmr.services.swing.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class CheckBoxTitledBorder extends ComponentTitledBorder {
    public static void main(String[] args) {
        final JPanel panel = new JPanel();
        panel.add(new JCheckBox("inner checkbox"));
        panel.setBorder(new CheckBoxTitledBorder(panel, " outer checkbox"));
        JFrame frame = new JFrame();
        Container contents = frame.getContentPane();
        contents.setLayout(new FlowLayout());
        contents.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public CheckBoxTitledBorder(final JComponent container, String checkboxText) {
        super(new JCheckBox(checkboxText), container, BorderFactory.createEtchedBorder());
        final JCheckBox checkBox = this.getCheckbox();
        checkBox.setSelected(true);
        checkBox.setFocusPainted(false);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enable = checkBox.isSelected();
                Component[] components = container.getComponents();
                for (Component component : components) {
                    component.setEnabled(enable);
                }
            }
        });
    }

    public JCheckBox getCheckbox() {
        return JCheckBox.class.cast(this.comp);
    }
}
