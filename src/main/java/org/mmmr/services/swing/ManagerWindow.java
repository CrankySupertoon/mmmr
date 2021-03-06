package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;
import org.mmmr.Mod;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.InstallationService;
import org.mmmr.services.Messages;
import org.mmmr.services.UtilityMethods;
import org.swingeasy.RoundedPanel;
import org.swingeasy.UIUtils;
import org.swingeasy.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class ManagerWindow extends JFrame {
    private class ModOption {
        private final Mod mod;

        public ModOption(Mod mod) {
            super();
            this.mod = mod;
        }

        public Mod getMod() {
            return this.mod;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(this.mod.getName()).append(" v").append(this.mod.getVersion()); //$NON-NLS-1$

            if (this.mod.isInstalled()) {
                sb.append(" [installed]"); //$NON-NLS-1$
            }

            File file = new File(ManagerWindow.this.cfg.getMods(), this.mod.getArchive());
            if (!file.exists()) {
                sb.append(" [archive not found]"); //$NON-NLS-1$
            }

            return sb.toString();
        }
    }

    private static final long serialVersionUID = -2874170242621940902L;

    private Config cfg;

    private final InstallationService iserv;

    private JavaOptionsWindow javaOptionsWindow;

    public ManagerWindow(Config cfg) {
        this.cfg = cfg;
        this.iserv = new InstallationService(cfg);
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());
        this.setUndecorated(true);
        RoundedPanel mainpanel = new RoundedPanel(new GridLayout(-1, 1));
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label);
        this.addActions(mainpanel);
        JButton quit = new JButton(Messages.getString("ManagerWindow.exit")); //$NON-NLS-1$
        quit.setFont(cfg.getFontLarge());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerWindow.this.dispose();
                System.exit(0);
            }
        });
        mainpanel.add(quit);
        this.setResizable(false);
        UIUtils.translucent(this);
        this.pack();
        this.setSize(800, this.getHeight());
        UIUtils.rounded(this);
        this.setVisible(true);
    }

    private void addActions(Container mainpanel) {
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.change_startup")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (ManagerWindow.this.javaOptionsWindow == null) {
                            ManagerWindow.this.javaOptionsWindow = new JavaOptionsWindow(ManagerWindow.this.cfg, ManagerWindow.this.getSize());
                        }
                        ManagerWindow.this.javaOptionsWindow.setLocationRelativeTo(ManagerWindow.this);
                        ManagerWindow.this.javaOptionsWindow.selectDefault();
                        ManagerWindow.this.javaOptionsWindow.setVisible(true);
                    } catch (Exception ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.install_optifine")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ManagerWindow.this.performanceMod();
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.un_install_reorder")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ModOptionsWindow modOptionsWindow = new ModOptionsWindow(ManagerWindow.this.cfg);
                    modOptionsWindow.setLocationRelativeTo(ManagerWindow.this);
                    modOptionsWindow.setVisible(true);
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.change_sex")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO change
                    UtilityMethods.showWarning(ManagerWindow.this.cfg, Messages.getString("ManagerWindow.change_sex"), "Not implemented yet."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.backup_restore")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Backup and restore worlds/stats/etc.
                    UtilityMethods.showWarning(ManagerWindow.this.cfg, Messages.getString("ManagerWindow.backup_restore"), "Not implemented yet."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.hd_font")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    HDFontWindow hdFontWindow = new HDFontWindow(ManagerWindow.this.cfg);
                    hdFontWindow.setLocationRelativeTo(ManagerWindow.this);
                    hdFontWindow.setVisible(true);
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.links")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        FavoriteLinksWindow window = new FavoriteLinksWindow(ManagerWindow.this.cfg);
                        window.setLocationRelativeTo(ManagerWindow.this);
                        window.setVisible(true);
                    } catch (Exception ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.open_minecraft_dir")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().open(ManagerWindow.this.cfg.getMcBaseFolder());
                    } catch (IOException ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton(Messages.getString("ManagerWindow.change_logging")); //$NON-NLS-1$
            comp.setFont(this.cfg.getFontLarge());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Level[] levels = { Level.TRACE, Level.DEBUG, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF };
                        Level level = UtilityMethods.showOptions(
                                ManagerWindow.this.cfg,
                                Messages.getString("ManagerWindow.change_logging_level"), Messages.getString("ManagerWindow.change_logging_level"), levels, //$NON-NLS-1$ //$NON-NLS-2$
                                org.apache.log4j.Logger.getRootLogger().getLevel());
                        if (level != null) {
                            ManagerWindow.this.cfg.setProperty("logging.level", String.valueOf(level)); //$NON-NLS-1$
                            org.apache.log4j.Logger.getRootLogger().setLevel(level);
                            Enumeration<?> allAppenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
                            while (allAppenders.hasMoreElements()) {
                                Appender appender = Appender.class.cast(allAppenders.nextElement());
                                LevelRangeFilter filter = LevelRangeFilter.class.cast(appender.getFilter());
                                filter.setLevelMin(level);
                            }
                        }
                    } catch (Exception ex) {
                        ExceptionAndLogHandler.log(ex);
                    }
                }
            });
            mainpanel.add(comp);
        }
    }

    private void performanceMod() {
        try {
            File[] modxmls = this.cfg.getMods().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().contains("optifine") && !name.toLowerCase().contains("water") && name.endsWith(".xml"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });
            List<ModOption> options = new ArrayList<ModOption>();
            Mod installed = null;
            ModOption installedOption = null;
            for (File modxml : modxmls) {
                Mod availablemod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
                Mod installedmod = this.cfg.getDb().get(new Mod(availablemod.getName(), availablemod.getVersion()));
                if ((installedmod != null) && installedmod.isInstalled()) {
                    installed = installedmod;
                }
                Mod mod = installedmod != null ? installedmod : availablemod;
                ModOption modoption = new ModOption(mod);
                if ((installedmod != null) && installedmod.isInstalled()) {
                    installedOption = modoption;
                }
                options.add(modoption);
            }
            if (options.size() == 0) {
                UtilityMethods.showInformation(this.cfg,
                        Messages.getString("ManagerWindow.install_optifine"), Messages.getString("ManagerWindow.no_compatible_version")); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            ModOption[] selectionValues = options.toArray(new ModOption[options.size()]);
            ModOption selected = UtilityMethods
                    .showOptions(
                            this.cfg,
                            Messages.getString("ManagerWindow.install_optifine"), Messages.getString("ManagerWindow.select_version"), selectionValues, installedOption == null ? selectionValues[0] : installedOption); //$NON-NLS-1$ //$NON-NLS-2$
            if (selected != null) {
                Mod mod = selected.getMod();
                if (installed != null) {
                    if (mod.equals(installed)) {
                        // already installed, nothing to do
                    } else {
                        this.iserv.uninstallMod(installed);
                        this.iserv.installMod(mod);
                    }
                } else {
                    this.iserv.installMod(mod);
                }
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }
}
