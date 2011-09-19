package org.mmmr.services.swing;

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
import javax.swing.WindowConstants;

import org.mmmr.services.Config;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;

/**
 * @author Jurgen
 */
public class StatusWindow extends JFrame {
    public class StatusPanel extends JPanel implements StatusListener {
        private static final long serialVersionUID = 6282395518853882621L;

        private JLabel statuslabel;

        public StatusPanel(String text, String iconPath) {
            super(new BorderLayout());
            this.setOpaque(false);
            this.statuslabel = new JLabel(text, StatusWindow.getIcon(iconPath), SwingConstants.CENTER);
            this.statuslabel.setVerticalAlignment(SwingConstants.CENTER);
            this.statuslabel.setOpaque(false);
            this.statuslabel.setFont(StatusWindow.this.cfg.getFont18());
            this.add(this.statuslabel, BorderLayout.CENTER);
        }

        /**
         * 
         * @see javax.swing.JComponent#setEnabled(boolean)
         */
        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            for (int i = 0; i < this.getComponentCount(); i++) {
                this.getComponent(i).setEnabled(enabled);
            }
        }

        /**
         * 
         * @see org.mmmr.services.swing.StatusListener#setStatus(java.lang.String, java.lang.Boolean)
         */
        @Override
        public void setStatus(String text, Boolean success) {
            this.statuslabel.setText(text);
            this.statuslabel.setIcon(success == null ? StatusWindow.getIcon("images/waiting.png") : success ? StatusWindow.getIcon("images/ok.png")
                    : StatusWindow.getIcon("images/nok.png"));
        }
    }

    private static final long serialVersionUID = -4214468834438916001L;

    private static Icon getIcon(String path) {
        return new ImageIcon(StatusWindow.class.getClassLoader().getResource(path));
    }

    private Config cfg;

    private StatusPanel dbstatus;

    private JButton goOn = null;

    private StatusPanel libstatus;

    private StatusPanel mcstatus;

    private JButton quit = null;

    private StatusPanel xmlstatus;

    private StatusPanel ybstatus;

    public StatusWindow(final Config cfg) {
        this.cfg = cfg;
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());
        RoundedPanel mainpanel = new RoundedPanel(new GridLayout(-1, 1));
        mainpanel.setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);
        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
        mainpanel.add(label, BorderLayout.CENTER);
        String bullet = "images/waiting.png";
        this.libstatus = new StatusPanel("Program libraries", bullet);
        mainpanel.add(this.libstatus);
        this.dbstatus = new StatusPanel("Database and Hibernate", bullet);
        mainpanel.add(this.dbstatus);
        this.xmlstatus = new StatusPanel("XML service", bullet);
        mainpanel.add(this.xmlstatus);
        this.mcstatus = new StatusPanel("Minecraft", bullet);
        mainpanel.add(this.mcstatus);
        this.ybstatus = new StatusPanel("YogBox", bullet);
        mainpanel.add(this.ybstatus);
        this.goOn = new JButton("I'm ready to start adding mods :)");
        this.goOn.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
        this.goOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManagerWindow(cfg).setVisible(true);
                StatusWindow.this.dispose();
            }
        });
        this.goOn.setEnabled(false);
        this.quit = new JButton("Get me out of here :(");
        this.quit.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
        this.quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatusWindow.this.dispose();
            }
        });

        this.goOn.setMinimumSize(new Dimension(300, 30));
        this.quit.setMinimumSize(new Dimension(300, 30));
        this.goOn.setPreferredSize(new Dimension(300, 30));
        this.quit.setPreferredSize(new Dimension(300, 30));
        this.goOn.setSize(new Dimension(300, 30));
        this.quit.setSize(new Dimension(300, 30));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.add(this.goOn, null);
        btns.add(this.quit, null);
        mainpanel.add(btns);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        FancySwing.translucent(this);
        this.pack();
        this.setSize(800, this.getHeight());
        FancySwing.rounded(this);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    public Config getCfg() {
        return this.cfg;
    }

    public StatusPanel getDbstatus() {
        return this.dbstatus;
    }

    public StatusPanel getLibstatus() {
        return this.libstatus;
    }

    public StatusPanel getMcstatus() {
        return this.mcstatus;
    }

    public StatusPanel getXmlstatus() {
        return this.xmlstatus;
    }

    public StatusPanel getYbstatus() {
        return this.ybstatus;
    }

    public void setDbBStatus(String text, Boolean success) {
        this.dbstatus.setStatus(text, success);
    }

    public void setLibStatus(String text, Boolean success) {
        this.libstatus.setStatus(text, success);
    }

    public void setMcStatus(String text, Boolean success) {
        this.mcstatus.setStatus(text, success);
    }

    public void setReadyToGoOn() {
        this.goOn.setEnabled(true);
        this.goOn.grabFocus();
    }
}
