package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StatusFrame extends JFrame {
    public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 6282395518853882621L;

	private JLabel statuslabel;

	public StatusPanel(String text, String iconPath) {
	    super(new BorderLayout());
	    statuslabel = new JLabel("<html>" + text + "</html>", getIcon(iconPath), SwingConstants.CENTER);
	    statuslabel.setFont(cfg.getFont18());
	    add(statuslabel, BorderLayout.CENTER);
	}

	@Override
	public void setEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	    for (int i = 0; i < getComponentCount(); i++) {
		getComponent(i).setEnabled(enabled);
	    }
	}

	public void setStatus(String text, Boolean success) {
	    statuslabel.setText("<html>" + text + "</html>");
	    statuslabel.setIcon(success == null ? getIcon("images/bullet_yellow_x4.png") : success ? getIcon("images/bullet_green_x4.png") : getIcon("images/bullet_red_x4.png"));
	}
    }

    private static final long serialVersionUID = -4214468834438916001L;

    private static Icon getIcon(String path) {
	return new ImageIcon(StatusFrame.class.getClassLoader().getResource(path));
    }

    private Config cfg;
    public StatusPanel dbstatus;
    public StatusPanel libstatus;
    public StatusPanel mcstatus;

    public StatusPanel xmlstatus;

    public StatusPanel ybstatus;

    public StatusFrame(Config cfg) {
	this.cfg = cfg;
	Container contentPane = getContentPane();
	contentPane.setLayout(new GridLayout(-1, 1));
	String bullet = "images/bullet_yellow_x4.png";
	libstatus = new StatusPanel("Program libraries", bullet);
	contentPane.add(libstatus);
	dbstatus = new StatusPanel("Database and Hibernate", bullet);
	contentPane.add(dbstatus);
	xmlstatus = new StatusPanel("XML service", bullet);
	contentPane.add(xmlstatus);
	mcstatus = new StatusPanel("Minecraft", bullet);
	contentPane.add(mcstatus);
	ybstatus = new StatusPanel("YogBox", bullet);
	contentPane.add(ybstatus);
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public StatusPanel getYbstatus() {
	return ybstatus;
    }

    public void setDbBStatus(String text, Boolean success) {
	dbstatus.setStatus(text, success);
    }

    public void setLibStatus(String text, Boolean success) {
	libstatus.setStatus(text, success);
    }

    public void setMcStatus(String text, Boolean success) {
	mcstatus.setStatus(text, success);
    }
}
