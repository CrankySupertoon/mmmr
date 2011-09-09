package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Jurgen
 */
public class StatusFrame extends JFrame {
    public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 6282395518853882621L;

	private JLabel statuslabel;

	public StatusPanel(String text, String iconPath) {
	    super(new BorderLayout());
	    setOpaque(false);
	    statuslabel = new JLabel(text, getIcon(iconPath), SwingConstants.CENTER);
	    statuslabel.setVerticalAlignment(SwingConstants.CENTER);
	    statuslabel.setOpaque(false);
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
	    statuslabel.setText(text);
	    statuslabel.setIcon(success == null ? getIcon("images/bullet_yellow_x4.png") : success ? getIcon("images/bullet_green_x4.png") : getIcon("images/bullet_red_x4.png"));
	}
    }

    private static final long serialVersionUID = -4214468834438916001L;

    private static Icon getIcon(String path) {
	return new ImageIcon(StatusFrame.class.getClassLoader().getResource(path));
    }

    private Config cfg;

    public StatusPanel dbstatus;

    private JButton goOn = null;

    public StatusPanel libstatus;

    public StatusPanel mcstatus;

    private JButton quit = null;

    public StatusPanel xmlstatus;

    public StatusPanel ybstatus;

    public StatusFrame(final Config cfg) {
	this.cfg = cfg;
	setTitle("Minecraft Mod Manager Reloaded 1.0b For Minecraft 1.7.3b");
	JPanel contentPane = new JPanel();
	contentPane.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
	getContentPane().add(contentPane);
	JLabel label = new JLabel(getTitle());
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
	contentPane.add(label, BorderLayout.CENTER);
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
	goOn = new JButton("I'm ready to start adding mods :)");
	goOn.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
	goOn.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		new ManagerWindow(cfg).setVisible(true);
		dispose();
	    }
	});
	goOn.setEnabled(false);
	quit = new JButton("Get me out of here :(");
	quit.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
	quit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});

	goOn.setMinimumSize(new Dimension(300, 30));
	quit.setMinimumSize(new Dimension(300, 30));
	goOn.setPreferredSize(new Dimension(300, 30));
	quit.setPreferredSize(new Dimension(300, 30));
	goOn.setSize(new Dimension(300, 30));
	quit.setSize(new Dimension(300, 30));

	JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
	btns.add(goOn, null);
	btns.add(quit, null);
	contentPane.add(btns);

	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	contentPane.setDoubleBuffered(true);
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

    public void setReadyToGoOn() {
	quit.setEnabled(true);
	goOn.setEnabled(true);
	goOn.grabFocus();
    }
}
