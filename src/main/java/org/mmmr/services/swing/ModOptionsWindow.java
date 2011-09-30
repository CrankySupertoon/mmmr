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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;

import org.mmmr.Mod;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.InstallationService;
import org.mmmr.services.Messages;
import org.mmmr.services.ModList;
import org.mmmr.services.UtilityMethods;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableI;
import org.mmmr.services.swing.common.ETableRecord;
import org.mmmr.services.swing.common.ETableRecordBean;
import org.mmmr.services.swing.common.RoundedPanel;
import org.mmmr.services.swing.common.UIUtils;
import org.mmmr.services.swing.common.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class ModOptionsWindow extends JFrame {
    private static final long serialVersionUID = -3663235803657033008L;

    protected Config cfg;

    protected ETable options;

    private final InstallationService iserv;

    public ModOptionsWindow(final Config cfg) throws HeadlessException {
        this.cfg = cfg;
        this.iserv = new InstallationService(cfg);
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());

        RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label, BorderLayout.NORTH);

        final JTable jtable = this.getOptions();
        jtable.setBorder(BorderFactory.createRaisedBevelBorder());
        jtable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        mainpanel.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, -1));

        JButton resolve = new JButton(Messages.getString("ManagerWindow.resolve")); //$NON-NLS-1$
        resolve.setFont(cfg.getFontLarge());
        resolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Resolve mod conflicts.
                UtilityMethods.showWarning(cfg, Messages.getString("ManagerWindow.resolve"), "Not implemented yet."); //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
        actions.add(resolve);

        JButton commit = new JButton(Messages.getString("ModOptionsWindow.apply_changes")); //$NON-NLS-1$
        commit.setFont(cfg.getFontLarge());
        commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModOptionsWindow.this.applyChanges();
                ModOptionsWindow.this.dispose();
            }
        });
        actions.add(commit);

        JButton quit = new JButton(Messages.getString("ModOptionsWindow.do_not_make_changes")); //$NON-NLS-1$
        quit.setFont(cfg.getFontLarge());
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
        UIUtils.translucent(this);
        this.pack();
        this.setSize((int) jtable.getPreferredSize().getWidth(), this.getHeight());
        UIUtils.rounded(this);
        this.setResizable(false);
    }

    protected void applyChanges() {
        for (ETableRecord<ModOption> record : this.options.getEventSafe().getRecords()) {
            @SuppressWarnings("unchecked")
            ETableRecordBean<ModOption> eTableRecordBean = ETableRecordBean.class.cast(record);
            if (!eTableRecordBean.hasChanged("installed")) {
                continue;
            }
            Mod inTable = Mod.class.cast(eTableRecordBean.getBean());
            if (!inTable.isInstalled()) {
                ExceptionAndLogHandler.log("uninstall mod: " + inTable); //$NON-NLS-1$
                this.iserv.uninstallMod(this.cfg.getDb().refresh(inTable));
            }
        }
        for (ETableRecord<ModOption> record : this.options.getEventSafe().getRecords()) {
            Mod inTable = Mod.class.cast(ETableRecordBean.class.cast(record).getBean());
            Mod inDb = this.cfg.getDb().get(inTable);
            if ((inDb != null) && (inDb.getInstallOrder() != inTable.getInstallOrder())) {
                // TODO change load order: implement
                ExceptionAndLogHandler.log("change load order: " + inDb.getInstallOrder() + " // " + inTable); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        for (ETableRecord<ModOption> record : this.options.getEventSafe().getRecords()) {
            Mod inTable = Mod.class.cast(ETableRecordBean.class.cast(record).getBean());
            Mod inDb = this.cfg.getDb().get(inTable);
            if ((inDb == null) && inTable.isInstalled()) {
                ExceptionAndLogHandler.log("install mod: " + inTable); //$NON-NLS-1$
                try {
                    this.iserv.installMod(inTable);
                } catch (IOException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        }
    }

    protected JTable getOptions() {
        // update mod configuration from server
        try {
            ModList.update(this.cfg);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }

        ETableConfig configuration = new ETableConfig(true, false, true, true, false, true, false, true);
        this.options = new ETable(configuration);
        this.options.setFont(this.cfg.getFontTable());
        final ETableI safetable = this.options.getEventSafe();
        final List<String> orderedFields = new ArrayList<String>();
        final ETableHeaders headers = new ETableHeaders();

        this.options.setRowHeight(28);

        // click on column actions
        this.options.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {
                        int row = ModOptionsWindow.this.options.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        int col = ModOptionsWindow.this.options.columnAtPoint(e.getPoint());
                        if (col == -1) {
                            return;
                        }
                        Object columnValueAtVisualColumn = safetable.getColumnValueAtVisualColumn(col);
                        if (Messages.getString("ModOptionsWindow.url").equals(columnValueAtVisualColumn)) { //$NON-NLS-1$
                            String url = String.valueOf(safetable.getRecordAtVisualRow(row).get(col));
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(URI.create(url));
                            } else {
                                UtilityMethods.showWarning(ModOptionsWindow.this.cfg,
                                        "", Messages.getString("ModOptionsWindow.not_supported_visit_site") + url); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                        if (Messages.getString("ModOptionsWindow.modArchive").equals(columnValueAtVisualColumn)) { //$NON-NLS-1$
                            Boolean archiveFound = (Boolean) safetable.getRecordAtVisualRow(row).get(col);
                            if (!Boolean.TRUE.equals(archiveFound)) {
                                int urlcol = orderedFields.indexOf("url");
                                String url = String.valueOf(safetable.getRecordAtVisualRow(row).get(urlcol));
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(URI.create(url));
                                } else {
                                    UtilityMethods.showWarning(ModOptionsWindow.this.cfg,
                                            "", Messages.getString("ModOptionsWindow.not_supported_visit_site") + url); //$NON-NLS-1$ //$NON-NLS-2$
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });

        headers.add(Messages.getString("ModOptionsWindow.modArchive"), Boolean.class, false); //$NON-NLS-1$
        orderedFields.add("modArchive"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.installed"), Boolean.class, true); //$NON-NLS-1$
        orderedFields.add("installed"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.name"), String.class, false); //$NON-NLS-1$
        orderedFields.add("name"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.version"), String.class, false); //$NON-NLS-1$
        orderedFields.add("version"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.description"), String.class, false); //$NON-NLS-1$
        orderedFields.add("description"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.install_order"), Integer.class, false); //$NON-NLS-1$
        orderedFields.add("installOrder"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.install_date"), Date.class, false); //$NON-NLS-1$
        orderedFields.add("installationDate"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.url"), String.class, false); //$NON-NLS-1$
        orderedFields.add("url"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.updated"), Boolean.class, false); //$NON-NLS-1$
        orderedFields.add("updated"); //$NON-NLS-1$

        safetable.setHeaders(headers);

        File[] modxmls = this.cfg.getMods().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!name.endsWith(".xml")) { //$NON-NLS-1$
                    return false;
                }
                if (name.toLowerCase().contains("optifine")) { //$NON-NLS-1$
                    return false;
                }
                if (name.toLowerCase().contains("yogbox")) { //$NON-NLS-1$
                    return false;
                }
                if (name.toLowerCase().contains("gender")) { //$NON-NLS-1$
                    return false;
                }
                return true;
            }
        });

        List<ETableRecord<ModOption>> records = new ArrayList<ETableRecord<ModOption>>();
        // add installed mods (read from database) to list
        List<Mod> installedMods = this.cfg.getDb().all(Mod.class);
        for (Mod installedMod : installedMods) {
            records.add(new ETableRecordBean<ModOption>(orderedFields, new ModOption(this.cfg, installedMod)));
        }
        for (File modxml : modxmls) {
            ExceptionAndLogHandler.log(modxml);
            try {
                Mod availableMod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
                // mod not fit for mc version, do not show mod configuration
                if ((availableMod.getMcVersionDependency() != null) && !availableMod.getMcVersionDependency().contains("?")) { //$NON-NLS-1$
                    if (!availableMod.getMcVersionDependency().equals(this.cfg.getMcVersion())) {
                        continue;
                    }
                }
                // mod already added to list (read from database), do not show this configuration
                if (installedMods.contains(availableMod)) {
                    continue;
                }
                // add mod configuration
                records.add(new ETableRecordBean<ModOption>(orderedFields, new ModOption(this.cfg, availableMod)));
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }

        // sort, installed on top ordered by install order, then the others by name
        Collections.sort(records, new Comparator<ETableRecord<ModOption>>() {
            @Override
            public int compare(ETableRecord<ModOption> o1, ETableRecord<ModOption> o2) {
                Mod m1 = o1.getBean().getMod();
                Mod m2 = o2.getBean().getMod();
                if (m1.isInstalled() && !m2.isInstalled()) {
                    return -1;
                } else if (!m1.isInstalled() && m2.isInstalled()) {
                    return 1;
                } else if (m1.isInstalled() && m2.isInstalled()) {
                    return -m1.getInstallOrder() + m2.getInstallOrder();
                } else {
                    return m1.getName().compareToIgnoreCase(m2.getName());
                }
            }
        });

        for (ETableRecord<ModOption> record : records) {
            safetable.addRecord(record);
        }

        for (int i = 0; i < orderedFields.size(); i++) {
            this.options.packColumn(i, 8);
        }

        TableColumn col = this.options.getColumnModel().getColumn(orderedFields.indexOf("url")); //$NON-NLS-1$
        col.setPreferredWidth(250);
        col.setWidth(250);
        col.setMaxWidth(250);

        return this.options;
    }
}
