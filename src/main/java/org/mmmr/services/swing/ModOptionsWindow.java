package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
import org.mmmr.services.swing.common.ETableRecord;
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
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        final JTable jtable = this.getOptions();
        jtable.setBorder(BorderFactory.createRaisedBevelBorder());
        jtable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        mainpanel.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, -1));

        JButton commit = new JButton("Apply changes.");
        commit.setFont(cfg.getFont18());
        commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO make changes (installing/deinstalling/changing order)
                ModOptionsWindow.this.dispose();
            }
        });
        actions.add(commit);

        JButton quit = new JButton("Do not make any changes.");
        quit.setFont(cfg.getFont18());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModOptionsWindow.this.dispose();
            }
        });
        actions.add(quit);

        mainpanel.add(actions, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        FancySwing.translucent(this);
        this.pack();
        this.setSize((int) jtable.getPreferredSize().getWidth(), this.getHeight());
        FancySwing.rounded(this);
        this.setResizable(false);
    }

    private JTable getOptions() {
        ETableConfig configuration = new ETableConfig(true, false, true, true, false, true, false, true);
        final ETable options = new ETable(configuration);
        final ETableI safetable = options.getEventSafe();
        final List<String> orderedFields = new ArrayList<String>();

        options.setRowHeight(28);

        // click on column actions
        options.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
                        int row = options.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        int col = options.columnAtPoint(e.getPoint());
                        if (col == -1) {
                            return;
                        }
                        if ("url".equals(safetable.getColumnValueAtVisualColumn(col))) {
                            String url = String.valueOf(safetable.getRecordAtVisualRow(row).get(col));
                            Desktop.getDesktop().browse(URI.create(url));
                        }
                    }
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });

        ETableHeaders headers = new ETableHeaders();

        // 0
        headers.add("Installed", Boolean.class, true);
        orderedFields.add("installed");

        // 1
        headers.add("Name", String.class, false);
        orderedFields.add("name");

        // 2
        headers.add("Version", String.class, false);
        orderedFields.add("version");

        // 3
        headers.add("Description", String.class, false);
        orderedFields.add("description");

        // 4
        headers.add("Install order", Integer.class, false);
        orderedFields.add("installOrder");

        // 5
        headers.add("Install date", Date.class, false);
        orderedFields.add("installationDate");

        // 6
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
                if (name.toLowerCase().contains("gender")) {
                    return false;
                }
                return true;
            }
        });

        List<ETableRecord> records = new ArrayList<ETableRecord>();
        // add installed mods (read from database) to list
        List<Mod> installedMods = this.cfg.getDb().hql("from Mod", Mod.class);
        for (Mod installedMod : installedMods) {
            records.add(new ETableRecordBean(orderedFields, installedMod));
        }
        for (File modxml : modxmls) {
            try {
                Mod availableMod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
                // mod not fit for mc version, do not show mod configuration
                if ((availableMod.getMcVersionDependency() != null) && !availableMod.getMcVersionDependency().contains("?")) {
                    if (!availableMod.getMcVersionDependency().equals(this.cfg.getMcVersion())) {
                        continue;
                    }
                }
                // mod already added to list (read from database), do not show this configuration
                if (installedMods.contains(availableMod)) {
                    continue;
                }
                // add mod configuration
                records.add(new ETableRecordBean(orderedFields, availableMod));
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }

        // sort, installed on top ordered by install order, then the others by name
        Collections.sort(records, new Comparator<ETableRecord>() {
            @Override
            public int compare(ETableRecord o1, ETableRecord o2) {
                Mod m1 = Mod.class.cast(o1.getBean());
                Mod m2 = Mod.class.cast(o2.getBean());
                if (m1.isInstalled() && !m2.isInstalled()) {
                    return -1;
                } else if (!m1.isInstalled() && m2.isInstalled()) {
                    return 1;
                } else if (m1.isInstalled() && m2.isInstalled()) {
                    return m2.getInstallOrder() - m1.getInstallOrder();
                } else {
                    return m1.getName().compareToIgnoreCase(m2.getName());
                }
            }
        });

        for (ETableRecord record : records) {
            safetable.addRecord(record);
        }

        for (int i = 0; i < orderedFields.size(); i++) {
            options.packColumn(i, 8);
        }

        return options;
    }
}
