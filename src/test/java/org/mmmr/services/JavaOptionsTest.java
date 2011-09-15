package org.mmmr.services;

import javax.swing.UIManager;

public class JavaOptionsTest {
    public static void main(String[] args) {
	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	    JavaOptionsWindow javaOptionsWindow = new JavaOptionsWindow(null);
	    javaOptionsWindow.packColumns();
	    javaOptionsWindow.setVisible(true);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
