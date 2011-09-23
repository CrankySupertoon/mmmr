package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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
import org.mmmr.services.IOMethods;
import org.mmmr.services.InstallationService;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;

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
            StringBuilder sb = new StringBuilder(this.mod.getName()).append(" v").append(this.mod.getVersion());

            if (this.mod.isInstalled()) {
                sb.append(" [installed]");
            }

            File file = new File(ManagerWindow.this.cfg.getMods(), this.mod.getArchive());
            if (!file.exists()) {
                sb.append(" [archive not found]");
            }

            return sb.toString();
        }
    }

    private static final long serialVersionUID = -2874170242621940902L;

    private Config cfg;

    private InstallationService iserv = new InstallationService();

    private JavaOptionsWindow javaOptionsWindow;

    public ManagerWindow(Config cfg) {
        this.cfg = cfg;
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
        label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
        mainpanel.add(label);
        this.addActions(mainpanel);
        JButton quit = new JButton("Get me out of here :(");
        quit.setFont(cfg.getFont18());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerWindow.this.dispose();
            }
        });
        mainpanel.add(quit);
        this.setResizable(false);
        FancySwing.translucent(this);
        this.pack();
        this.setSize(800, this.getHeight());
        FancySwing.rounded(this);
        this.setVisible(true);
    }

    private void addActions(Container mainpanel) {
        {
            JButton comp = new JButton("Change startup configuration (performance related).");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (ManagerWindow.this.javaOptionsWindow == null) {
                            ManagerWindow.this.javaOptionsWindow = new JavaOptionsWindow(ManagerWindow.this.cfg, ManagerWindow.this.getSize());
                            ManagerWindow.this.javaOptionsWindow.packColumns();
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
            JButton comp = new JButton("Install OptiFine (performance mod & HD texture enabler).");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ManagerWindow.this.performanceMod();
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton("(Un)install mods and change order.");
            comp.setFont(this.cfg.getFont18());
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
            JButton comp = new JButton("Resolve mod conflicts.");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Resolve mod conflicts.
                    IOMethods.showWarning(ManagerWindow.this.cfg, "Resolve mod conflicts.", "Not implemented yet.");
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton("Change sex.");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO change
                    IOMethods.showWarning(ManagerWindow.this.cfg, "Change sex.", "Not implemented yet.");
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton("Backup and restore worlds/stats/etc.");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Backup and restore worlds/stats/etc.
                    IOMethods.showWarning(ManagerWindow.this.cfg, "Backup and restore worlds/stats/etc.", "Not implemented yet.");
                }
            });
            mainpanel.add(comp);
        }
        {
            JButton comp = new JButton("Change MMMR logging level.");
            comp.setFont(this.cfg.getFont18());
            comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Level[] levels = { Level.TRACE, Level.DEBUG, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF };
                        Level level = IOMethods.showOptions(ManagerWindow.this.cfg, "Choose logging level.", "Choose logging level.", levels,
                                org.apache.log4j.Logger.getRootLogger().getLevel());
                        if (level != null) {
                            ManagerWindow.this.cfg.setProperty("logging.level", String.valueOf(level));
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
                    return name.toLowerCase().contains("optifine") && name.endsWith(".xml");
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
                IOMethods.showInformation(this.cfg, "OptiFine.", "No compatible OptiFine mod configuration found.");
                return;
            }
            ModOption[] selectionValues = options.toArray(new ModOption[options.size()]);
            ModOption selected = installedOption == null ? selectionValues[0] : installedOption;
            selected = IOMethods.showOptions(this.cfg, "OptiFine.", "Select a version.", selectionValues, selected);
            if (selected != null) {
                Mod mod = ModOption.class.cast(selected).getMod();
                if (installed != null) {
                    if (mod.equals(installed)) {
                        // already installed, nothing to do
                    } else {
                        this.iserv.uninstallMod(this.cfg, installed);
                        this.iserv.installMod(this.cfg, mod);
                    }
                } else {
                    this.iserv.installMod(this.cfg, mod);
                }
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }
}
