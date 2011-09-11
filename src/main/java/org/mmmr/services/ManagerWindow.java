package org.mmmr.services;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;
import org.mmmr.Mod;

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

    public ManagerWindow(Config cfg) {
	this.cfg = cfg;
	this.setTitle("Minecraft Mod Manager Reloaded 1.0b For Minecraft 1.7.3b");
	this.setUndecorated(true);
	Container cp = this.getContentPane();
	JPanel panel = new JPanel(new GridLayout(-1, 1));
	panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
	cp.add(panel, BorderLayout.CENTER);
	cp = panel;
	JLabel label = new JLabel(this.getTitle());
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
	cp.add(label);
	this.addActions(cp);
	JButton quit = new JButton("Get me out of here :(");
	quit.setFont(cfg.getFont18());
	quit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		ManagerWindow.this.dispose();
	    }
	});
	cp.add(quit);
	this.setLocationRelativeTo(null);
	this.setResizable(false);
	FancySwing.translucent(this);
	this.pack();
	this.setSize(800, this.getHeight());
	this.setLocationRelativeTo(null);
	FancySwing.rounded(this);
	this.setVisible(true);
    }

    private void addActions(Container cp) {
	{
	    JButton comp = new JButton("Change startup configuration (performance related)");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		    JOptionPane.showMessageDialog(null, "Not implemented yet.");
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Install OptiFine (performance mod & HD texture enabler)");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    ManagerWindow.this.performanceMod();
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Install mods");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    ManagerWindow.this.installMods();
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Uninstall mods");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		    JOptionPane.showMessageDialog(null, "Not implemented yet.");
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change mod order and resolve conflicts");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		    JOptionPane.showMessageDialog(null, "Not implemented yet.");
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change sex");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		    JOptionPane.showMessageDialog(null, "Not implemented yet.");
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Backup and restore worlds/stats/etc");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		    JOptionPane.showMessageDialog(null, "Not implemented yet.");
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change MMMR logging level");
	    comp.setFont(this.cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    try {
			Level[] levels = { Level.TRACE, Level.DEBUG, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF };
			Level level = Level.class.cast(JOptionPane.showInputDialog(null, "Choose logging level", "Logging", JOptionPane.QUESTION_MESSAGE, null, levels,
				org.apache.log4j.Logger.getRootLogger().getLevel()));
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
		    } catch (Exception e2) {
			e2.printStackTrace();
		    }
		}
	    });
	    cp.add(comp);
	}
    }

    private void installMods() {
	try {
	    File[] modxmls = this.cfg.getMods().listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    if (!name.endsWith(".xml")) {
			return false;
		    }
		    if (name.toLowerCase().contains("optifine")) {
			return false;
		    }
		    if (name.toLowerCase().contains("yogbox")) {
			return false;
		    }
		    return true;
		}
	    });
	    List<ModOption> options = new ArrayList<ModOption>();
	    for (File modxml : modxmls) {
		Mod availablemod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
		Mod installedmod = this.cfg.getDb().get(new Mod(availablemod.getName(), availablemod.getVersion()));
		if ((installedmod != null) && installedmod.isInstalled()) {
		    continue;
		}
		options.add(new ModOption(availablemod));
	    }
	    ModOption selected = ModOption.class.cast(JOptionPane.showInputDialog(null, "Select a version", "Select a version", JOptionPane.QUESTION_MESSAGE, null,
		    options.toArray(), options.get(0)));
	    if (selected != null) {
		this.iserv.installMod(this.cfg.getDb(), selected.getMod(), this.cfg.getMods(), this.cfg.getTmp(), this.cfg.getMcBaseFolder());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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
	    ModOption[] selectionValues = options.toArray(new ModOption[options.size()]);
	    ModOption selected = installedOption == null ? selectionValues[0] : installedOption;
	    selected = ModOption.class.cast(JOptionPane
		    .showInputDialog(null, "Select a version", "Select a version", JOptionPane.QUESTION_MESSAGE, null, selectionValues, selected));
	    if (selected != null) {
		Mod mod = ModOption.class.cast(selected).getMod();
		if (installed != null) {
		    if (mod.equals(installed)) {
			// already installed, nothing to do
		    } else {
			this.iserv.uninstallMod(this.cfg.getDb(), installed, this.cfg.getMods(), this.cfg.getTmp(), this.cfg.getMcBaseFolder());
			this.iserv.installMod(this.cfg.getDb(), mod, this.cfg.getMods(), this.cfg.getTmp(), this.cfg.getMcBaseFolder());
		    }
		} else {
		    this.iserv.installMod(this.cfg.getDb(), mod, this.cfg.getMods(), this.cfg.getTmp(), this.cfg.getMcBaseFolder());
		}
	    }
	} catch (Exception e2) {
	    e2.printStackTrace();
	}
    }
}
