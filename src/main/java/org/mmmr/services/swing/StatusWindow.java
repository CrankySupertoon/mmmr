package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.mmmr.services.Config;
import org.mmmr.services.Messages;
import org.mmmr.services.UtilityMethods;
import org.swingeasy.RoundedPanel;
import org.swingeasy.UIUtils;
import org.swingeasy.UIUtils.MoveMouseListener;

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
            this.statuslabel = new JLabel(text, UtilityMethods.getIcon(iconPath), SwingConstants.CENTER);
            this.statuslabel.setVerticalAlignment(SwingConstants.CENTER);
            this.statuslabel.setOpaque(false);
            this.statuslabel.setFont(StatusWindow.this.cfg.getFontLarge());
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
            this.statuslabel
                    .setIcon(success == null ? UtilityMethods.getIcon("images/waiting.png") : success ? UtilityMethods.getIcon("images/ok.png") //$NON-NLS-1$ //$NON-NLS-2$
                            : UtilityMethods.getIcon("images/nok.png")); //$NON-NLS-1$
        }
    }

    private static final long serialVersionUID = -4214468834438916001L;

    private Config cfg;

    private StatusPanel dbstatus;

    // private JButton goOn = null;

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
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);
        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label, BorderLayout.CENTER);
        String bullet = "images/waiting.png"; //$NON-NLS-1$
        this.libstatus = new StatusPanel(Messages.getString("StatusWindow.libraries"), bullet); //$NON-NLS-1$
        mainpanel.add(this.libstatus);
        this.dbstatus = new StatusPanel(Messages.getString("StatusWindow.db_hibernate"), bullet); //$NON-NLS-1$
        mainpanel.add(this.dbstatus);
        this.xmlstatus = new StatusPanel(Messages.getString("StatusWindow.xml_service"), bullet); //$NON-NLS-1$
        mainpanel.add(this.xmlstatus);
        this.mcstatus = new StatusPanel("Minecraft", bullet); //$NON-NLS-1$
        mainpanel.add(this.mcstatus);
        this.ybstatus = new StatusPanel("YogBox", bullet); //$NON-NLS-1$
        mainpanel.add(this.ybstatus);
        //        this.goOn = new JButton(Messages.getString("StatusWindow.to_mod_manager")); //$NON-NLS-1$
        // this.goOn.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
        // this.goOn.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // ManagerWindow managerWindow = new ManagerWindow(cfg);
        // managerWindow.setLocationRelativeTo(StatusWindow.this);
        // managerWindow.setVisible(true);
        // StatusWindow.this.dispose();
        // }
        // });
        // this.goOn.setEnabled(false);
        this.quit = new JButton(Messages.getString("StatusWindow.exit")); //$NON-NLS-1$
        this.quit.setFont(cfg.getFontLarge());
        this.quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatusWindow.this.dispose();
                System.exit(0);
            }
        });

        // this.goOn.setMinimumSize(new Dimension(300, 30));
        // this.quit.setMinimumSize(new Dimension(300, 30));
        // this.goOn.setPreferredSize(new Dimension(300, 30));
        // this.quit.setPreferredSize(new Dimension(300, 30));
        // this.goOn.setSize(new Dimension(300, 30));
        // this.quit.setSize(new Dimension(300, 30));

        // JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // btns.add(this.goOn, null);
        // btns.add(this.quit, null);
        mainpanel.add(this.quit);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        UIUtils.translucent(this);
        this.pack();
        this.setSize(800, this.getHeight());
        UIUtils.rounded(this);
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
        // this.goOn.setEnabled(true);
        // this.goOn.grabFocus();
        ManagerWindow managerWindow = new ManagerWindow(this.cfg);
        managerWindow.setLocationRelativeTo(StatusWindow.this);
        managerWindow.setVisible(true);
        StatusWindow.this.dispose();
    }
}
