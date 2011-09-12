package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Test {
    public static void main(String[] args) {
	try {
	    FancySwing.lookAndFeel();
	    final JDialog w = new JDialog((JFrame) null);
	    RoundedPanel panel = new RoundedPanel();
	    panel.setShady(false);
	    panel.setBackground((Color) UIManager.get("ToolTip.background"));
	    w.getContentPane().add(panel, BorderLayout.CENTER);
	    w.setUndecorated(true);
	    w.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    w.setSize(200, 100);
	    w.setLocationRelativeTo(null);
	    FancySwing.translucent(w, .85f);
	    FancySwing.rounded(w);
	    new FancySwing.MoveMouseListener(panel);
	    w.setVisible(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
