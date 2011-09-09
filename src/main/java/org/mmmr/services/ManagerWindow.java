package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ManagerWindow extends JFrame {
    private static final long serialVersionUID = -2874170242621940902L;

    private Config cfg;

    public ManagerWindow(Config cfg) {
	this.cfg = cfg;
	setTitle("Minecraft Mod Manager Reloaded 1.0b For Minecraft 1.7.3b");
	setUndecorated(true);
	Container cp = getContentPane();
	JPanel panel = new JPanel(new GridLayout(-1, 1));
	panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
	cp.add(panel, BorderLayout.CENTER);
	cp = panel;
	JLabel label = new JLabel(getTitle());
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
	cp.add(label);
	addActions(cfg, cp);
	JButton quit = new JButton("Get me out of here :(");
	quit.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
	quit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	cp.add(quit);
	setLocationRelativeTo(null);
	setResizable(false);
	FancySwing.translucent(this);
	pack();
	setSize(800, getHeight());
	setLocationRelativeTo(null);
	FancySwing.rounded(this);
	setVisible(true);
    }

    private void addActions(Config cfg, Container cp) {
	// just testing
	for (int j = 0; j < 8; j++) {
	    JButton comp = new JButton("" + j);
	    comp.setFont(cfg.getFont18());
	    cp.add(comp);
	}
    }
}
