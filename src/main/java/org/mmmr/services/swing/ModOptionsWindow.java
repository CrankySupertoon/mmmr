package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.mmmr.Mod;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableI;
import org.mmmr.services.swing.common.ETableRecordBean;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;

public class ModOptionsWindow extends JFrame {
    private static final long serialVersionUID = -3663235803657033008L;

    private Config cfg;

    public ModOptionsWindow(final Config cfg) throws HeadlessException {
        this.cfg = cfg;
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());

        RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        final JTable jtable = this.getOptions();
        jtable.setBorder(BorderFactory.createRaisedBevelBorder());
        jtable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        mainpanel.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JButton quit = new JButton("Select an option and click here.");
        quit.setFont(cfg.getFont18());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModOptionsWindow.this.dispose();
            }
        });
        mainpanel.add(quit, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        FancySwing.translucent(this);
        this.pack();
        this.setSize((int) jtable.getPreferredSize().getWidth(), this.getHeight());
        FancySwing.rounded(this);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    private JTable getOptions() {
        ETableConfig configuration = new ETableConfig(true, true, true, true, true, true, false);
        ETable options = new ETable(configuration);
        ETableI safetable = options.getEventSafe();
        List<String> orderedFields = new ArrayList<String>();
        ETableHeaders headers = new ETableHeaders();

        headers.add("Name", String.class, false);
        orderedFields.add("name");

        headers.add("Version", String.class, false);
        orderedFields.add("version");

        headers.add("Installed", Boolean.class, true);
        orderedFields.add("installed");

        headers.add("Description", String.class, false);
        orderedFields.add("description");

        headers.add("Install order", Integer.class, false);
        orderedFields.add("installOrder");

        headers.add("Install date", Date.class, false);
        orderedFields.add("installationDate");

        headers.add("Link", String.class, false);
        orderedFields.add("url");

        safetable.setHeaders(headers);

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
        for (File modxml : modxmls) {
            try {
                Mod availablemod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
                // mod not fit for mc version
                if ((availablemod.getMcVersionDependency() != null) && !availablemod.getMcVersionDependency().contains("?")) {
                    if (!availablemod.getMcVersionDependency().equals(this.cfg.getMcVersion())) {
                        continue;
                    }
                }
                Mod installedmod = this.cfg.getDb().get(new Mod(availablemod.getName(), availablemod.getVersion()));
                // mod already installed
                if ((installedmod != null) && installedmod.isInstalled()) {
                    continue;
                }
                safetable.addRecord(new ETableRecordBean(orderedFields, availablemod));
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }

        for (int i = 0; i < orderedFields.size(); i++) {
            options.packColumn(i, 4);
        }

        return options;
    }
}
