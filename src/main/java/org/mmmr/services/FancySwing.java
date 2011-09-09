package org.mmmr.services;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.sun.awt.AWTUtilities;

/**
 * http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/
 * 
 */
public class FancySwing {
    public static void lookAndFeel() {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void rounded(JFrame w) {
	if (AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSPARENT)) {
	    try {
		Shape shape = new RoundRectangle2D.Float(0, 0, w.getWidth(), w.getHeight(), 40, 40);
		AWTUtilitiesWrapper.setWindowShape(w, shape);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public static void translucent(JFrame w) {
	if (AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
	    try {
		AWTUtilitiesWrapper.setWindowOpacity(w, 0.8f);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
