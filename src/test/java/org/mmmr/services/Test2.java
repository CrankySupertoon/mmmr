package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Test2 {
    protected static JPanel getPanel() {
	@SuppressWarnings("serial")
	JPanel panel = new JPanel() {
	    @Override
	    protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
		    final int R = 200;
		    final int G = 200;
		    final int B = 240;

		    Paint p = new GradientPaint(0.0f, 0.0f, new Color(R, G, B, 0), this.getWidth(), this.getHeight(), new Color(R, G, B, 255), true);
		    Graphics2D g2d = (Graphics2D) g;
		    g2d.setPaint(p);
		    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		} else {
		    super.paintComponent(g);
		}
	    }
	};
	return panel;
    }

    public static void main(String[] args) {
	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	    JFrame ff = new JFrame();
	    JButton jb = new JButton("exit");
	    jb.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);

		}
	    });
	    ff.getContentPane().add(jb, BorderLayout.CENTER);
	    ff.pack();
	    ff.setVisible(true);
	    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    {
		JDialog d = new JDialog();
		JPanel panel = Test2.getPanel();
		d.getContentPane().add(panel, BorderLayout.CENTER);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.setUndecorated(true);
		d.setSize(100, 100);
		if (device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
		    d.setShape(new RoundRectangle2D.Double(0, 0, 100, 100, 15, 15));
		}
		if (device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
		    d.setOpacity(.75f);
		}
		d.setLocation(200, 200);
		d.setVisible(true);
	    }
	    {
		JDialog d = new JDialog();
		JPanel panel = new JPanel();
		d.getContentPane().add(panel, BorderLayout.CENTER);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.setUndecorated(true);
		d.setSize(100, 100);
		if (device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
		    d.setShape(new RoundRectangle2D.Double(0, 0, 100, 100, 15, 15));
		}
		if (device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
		    d.setOpacity(.75f);
		}
		d.setLocation(280, 220);
		d.setVisible(true);
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
