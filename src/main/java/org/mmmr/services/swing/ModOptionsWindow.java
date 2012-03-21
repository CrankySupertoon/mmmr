package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.mmmr.Mod;
import org.mmmr.Mode;
import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.InstallationService;
import org.mmmr.services.Messages;
import org.mmmr.services.ModList;
import org.mmmr.services.UtilityMethods;
import org.swingeasy.CheckBoxTitledBorder;
import org.swingeasy.ETable;
import org.swingeasy.ETable.ETableModel;
import org.swingeasy.ETableConfig;
import org.swingeasy.ETableHeaders;
import org.swingeasy.ETableI;
import org.swingeasy.ETableRecord;
import org.swingeasy.ETableRecordBean;
import org.swingeasy.RoundedPanel;
import org.swingeasy.TristateButtonModel;
import org.swingeasy.TristateCheckBox;
import org.swingeasy.TristateState;
import org.swingeasy.UIUtils;
import org.swingeasy.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class ModOptionsWindow extends JFrame {
    private class CustomMatcher extends ETable.Filter<ModOption> implements ItemListener, DocumentListener, ActionListener {
        private TristateCheckBox installed = new TristateCheckBox(Messages.getString("ManagerWindow.installed"), null, TristateState.INDETERMINATE); //$NON-NLS-1$

        private TristateCheckBox available = new TristateCheckBox(Messages.getString("ManagerWindow.available"), null, TristateState.INDETERMINATE); //$NON-NLS-1$

        private JTextField name = new JTextField(20);

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private JComboBox mode = new JComboBox(new Object[] { null, Mode.SSP, Mode.SMP });

        private JCheckBox parent;

        public CustomMatcher() {
            this.installed.addItemListener(this);
            this.available.addItemListener(this);
            this.name.getDocument().addDocumentListener(this);
            this.mode.addActionListener(this);
        }

        /**
         * 
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            this.fire();
        }

        /**
         * 
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        @Override
        public void changedUpdate(DocumentEvent e) {
            this.fire();
        }

        /**
         * 
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            this.fire();
        }

        /**
         * 
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            this.fire();
        }

        /**
         * 
         * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
         */
        @Override
        public boolean matches(ETableRecord<ModOption> it) {
            ETableRecord<ModOption> item = it;
            Boolean parentSelected = this.parent.isSelected();
            Boolean installedSelected = this.installed.getStateValue();
            Boolean availableSelected = this.available.getStateValue();
            Boolean archiveAvailable = item.getBean().isModArchive();
            Boolean modInstalled = item.getBean().getInstalled();
            boolean I = (installedSelected == null) || installedSelected.equals(modInstalled);
            boolean A = (availableSelected == null) || availableSelected.equals(archiveAvailable);
            boolean N = StringUtils.isBlank(this.name.getText())
                    || Pattern.compile(this.name.getText(), Pattern.CASE_INSENSITIVE).matcher(it.getBean().getName()).find();
            boolean M = (this.mode.getSelectedItem() == null) || this.mode.getSelectedItem().equals(it.getBean().getMode());
            boolean accept = !parentSelected || (I && A && N && M);
            ExceptionAndLogHandler.log("mod=" + item.getBean().getName() + "::" + !parentSelected + "|" + I + "&" + A + "&" + N + "&" + M);
            return accept;
        }

        /**
         * 
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            this.fire();
        }

        public void setParent(JCheckBox parent) {
            this.parent = parent;
            this.parent.addItemListener(this);
        }
    }

    private class UpdatedCellRenderer implements TableCellRenderer {
        TristateCheckBox delegate = new TristateCheckBox("");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Boolean b = (Boolean) value;
            if (b == null) {
                TristateButtonModel.class.cast(this.delegate.getModel()).setIndeterminate();
            } else {
                TristateButtonModel.class.cast(this.delegate.getModel()).setSelected(b);
            }
            return this.delegate;
        }

    }

    public static class Versions {
        protected SortedMap<String, Mod> versions_map = new TreeMap<String, Mod>();

        protected List<String> versions_list = new ArrayList<String>();

        protected String version;

        public void addVersion(Mod mod) {
            this.versions_map.put(mod.getVersion(), mod);
            this.versions_list.add(mod.getVersion());
            Collections.sort(this.versions_list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return -new CompareToBuilder().append(o1, o2).toComparison();
                }
            });
            this.version = null;
            for (Mod element : this.versions_map.values()) {
                if (element.getInstalled()) {
                    this.version = element.getVersion();
                }
            }
            if (this.version == null) {
                this.version = this.versions_list.get(0);
            }
        }

        public void clear() {
            this.versions_list.clear();
            this.versions_map.clear();
            this.version = null;
        }

        public Mod getInstalledMod() {
            for (Mod m : this.versions_map.values()) {
                if (m.getInstalled()) {
                    return m;
                }
            }
            return null;
        }

        public Mod getInstalledOrLatestMod() {
            Mod m = this.getInstalledMod();
            if (m == null) {
                m = this.getLatestMod();
            }
            return m;
        }

        public Mod getLatestMod() {
            return this.versions_map.get(this.versions_list.get(0));
        }

        public Mod getMod() {
            return this.versions_map.get(this.getVersion());
        }

        public String getVersion() {
            return this.version;
        }

        public Collection<String> getVersions() {
            return this.versions_list;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.getVersion();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static class VersionsTableCellEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 7902276317137342526L;

        private Versions versions;

        public VersionsTableCellEditor() {
            super(new JComboBox());

            final JComboBox comboBox = (JComboBox) this.editorComponent;
            comboBox.removeActionListener(this.delegate);

            this.delegate = new EditorDelegate() {
                private static final long serialVersionUID = 2353213356270929219L;

                @Override
                public Object getCellEditorValue() {
                    VersionsTableCellEditor.this.versions.setVersion((String) comboBox.getSelectedItem());
                    return VersionsTableCellEditor.this.versions;
                }

                @Override
                public void setValue(Object value) {
                    VersionsTableCellEditor.this.versions = (Versions) value;
                    DefaultComboBoxModel model = (DefaultComboBoxModel) comboBox.getModel();
                    model.removeAllElements();
                    for (String option : VersionsTableCellEditor.this.versions.getVersions()) {
                        model.addElement(option);
                    }
                    comboBox.setSelectedItem(VersionsTableCellEditor.this.versions.getVersion());
                    comboBox.setEnabled(VersionsTableCellEditor.this.versions.getVersions().size() > 1);
                }

                @Override
                public boolean shouldSelectCell(EventObject anEvent) {
                    if (anEvent instanceof MouseEvent) {
                        MouseEvent e = (MouseEvent) anEvent;
                        return e.getID() != MouseEvent.MOUSE_DRAGGED;
                    }
                    return true;
                }

                @Override
                public boolean stopCellEditing() {
                    if (comboBox.isEditable()) {
                        comboBox.actionPerformed(new ActionEvent(this, 0, ""));
                    }
                    return super.stopCellEditing();
                }
            };
            comboBox.addActionListener(this.delegate);
        }
    }

    public static class VersionsTableCellRenderer extends DefaultTableCellRenderer.UIResource {
        private static final long serialVersionUID = 6704921337811856316L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Versions versions = (Versions) value;
            if (versions.versions_list.size() > 1) {
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }
            return c;
        }
    }

    private static final long serialVersionUID = -3663235803657033008L;

    protected Config cfg;

    protected ETable<ModOption> options;

    private final InstallationService iserv;

    private CustomMatcher matcher;

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

        JPanel filters = new JPanel(new FlowLayout());
        CheckBoxTitledBorder checkboxBorder = new CheckBoxTitledBorder(filters, Messages.getString("ManagerWindow.filters"));//$NON-NLS-1$
        filters.setBorder(checkboxBorder);

        this.matcher.installed.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        this.matcher.available.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel namepanel = new JPanel(new FlowLayout());
        namepanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        namepanel.add(new JLabel(Messages.getString("ManagerWindow.name")));//$NON-NLS-1$
        namepanel.add(this.matcher.name);

        JPanel modepanel = new JPanel(new FlowLayout());
        modepanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modepanel.add(new JLabel(Messages.getString("ManagerWindow.mode")));//$NON-NLS-1$
        modepanel.add(this.matcher.mode);

        JPanel availablepanel = new JPanel(new FlowLayout());
        availablepanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        availablepanel.add(this.matcher.available);

        JPanel installedpanel = new JPanel(new FlowLayout());
        installedpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        installedpanel.add(this.matcher.installed);

        filters.add(installedpanel);
        filters.add(availablepanel);
        filters.add(namepanel);
        filters.add(modepanel);

        this.matcher.setParent(checkboxBorder.getCheckbox());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(filters, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(jtable), BorderLayout.CENTER);
        mainpanel.add(tablePanel, BorderLayout.CENTER);

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

        JButton open = new JButton(Messages.getString("ModOptionsWindow.open_mod_dir")); //$NON-NLS-1$
        open.setFont(cfg.getFontLarge());
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(cfg.getMods());
                } catch (IOException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        actions.add(open);

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

        this.matcher.fire();
    }

    protected void applyChanges() {
        for (ETableRecord<ModOption> record : this.options.getSimpleThreadSafeInterface().getRecords()) {
            ModOption modOption = record.getBean();
            Mod mod = modOption.getMod();
            if ((modOption.getInstalled() != mod.getInstalled()) && !modOption.getInstalled()) {
                ExceptionAndLogHandler.log("uninstall mod: " + mod); //$NON-NLS-1$
                try {
                    this.iserv.uninstallMod(this.cfg.getDb().refresh(mod));
                } catch (IOException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        }
        for (ETableRecord<ModOption> record : this.options.getSimpleThreadSafeInterface().getRecords()) {
            ModOption modOption = record.getBean();
            Mod mod = modOption.getMod();
            if (Boolean.TRUE.equals(modOption.getInstalled()) && Boolean.TRUE.equals(mod.getInstalled())) {
                if (modOption.getInstallOrder() != mod.getInstallOrder()) {
                    // TODO implement change load order
                    ExceptionAndLogHandler.log("change load order: " + mod.getInstallOrder() + " >> " + modOption.getInstallOrder() + " :: " + mod); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                }
            }
        }
        for (ETableRecord<ModOption> record : this.options.getSimpleThreadSafeInterface().getRecords()) {
            ModOption modOption = record.getBean();
            Mod mod = modOption.getMod();
            if ((modOption.getInstalled() != mod.getInstalled()) && modOption.getInstalled()) {
                ExceptionAndLogHandler.log("install mod: " + mod); //$NON-NLS-1$
                try {
                    this.iserv.installMod(mod);
                } catch (IOException ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        }
        UtilityMethods.showInformation(this.cfg, Messages.getString("ModOptionsWindow.apply_changes"), //$NON-NLS-1$
                Messages.getString("ModOptionsWindow.apply_changes_done")); //$NON-NLS-1$
    }

    protected JTable getOptions() {
        // update mod configuration from server
        try {
            ModList.update(this.cfg);
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }

        ETableConfig configuration = new ETableConfig(true, false, false, true, false, true, false, true);
        this.matcher = new CustomMatcher();
        this.options = new ETable(configuration, this.matcher);

        this.options.setFont(this.cfg.getFontTable());
        final ETableI safetable = this.options.getSimpleThreadSafeInterface();
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
                        @SuppressWarnings("unchecked")
                        ETableRecord<ModOption> recordAtVisualRow = safetable.getRecordAtVisualRow(row);
                        ModOption mod = recordAtVisualRow.getBean();
                        if (Messages.getString("ModOptionsWindow.url").equals(columnValueAtVisualColumn)) { //$NON-NLS-1$
                            String url = String.valueOf(recordAtVisualRow.get(col));
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(URI.create(url));
                            }
                        }
                        if (Messages.getString("ModOptionsWindow.modArchive").equals(columnValueAtVisualColumn)) { //$NON-NLS-1$
                            Boolean archiveFound = (Boolean) recordAtVisualRow.get(col);
                            if (!Boolean.TRUE.equals(archiveFound)) {
                                int urlcol = orderedFields.indexOf("url");
                                String url = String.valueOf(recordAtVisualRow.get(urlcol));
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(URI.create(url));
                                }
                            }
                        }
                        if (Messages.getString("ModOptionsWindow.updated").equals(columnValueAtVisualColumn)) { //$NON-NLS-1$
                            Boolean updated = (Boolean) recordAtVisualRow.get(col);
                            if (updated == null) {
                                // FIXME if site gives 502 bad gateway this seems to hang the application
                                // do this asyn and with a timeout and block functionality when busy per record
                                Boolean newValue = mod.checkIfUpdated();
                                recordAtVisualRow.set(orderedFields.indexOf("updated"), newValue);
                                ETableModel.class.cast(ModOptionsWindow.this.options.getModel()).fireTableCellUpdated(row,
                                        orderedFields.indexOf("updated"));
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

        headers.add(Messages.getString("ModOptionsWindow.version"), Versions.class, true); //$NON-NLS-1$
        orderedFields.add("versions"); //$NON-NLS-1$

        headers.add(Messages.getString("ModOptionsWindow.mode"), String.class, false); //$NON-NLS-1$
        orderedFields.add("mode"); //$NON-NLS-1$        

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

        this.options.setDefaultEditor(Versions.class, new VersionsTableCellEditor());
        this.options.setDefaultRenderer(Versions.class, new VersionsTableCellRenderer());

        this.options.getColumnModel().getColumn(orderedFields.indexOf("updated")).setCellRenderer(new UpdatedCellRenderer());

        File[] modxmls = this.cfg.getMods().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!name.endsWith(".xml")) { //$NON-NLS-1$
                    return false;
                }
                return true;
            }
        });

        List<ETableRecord<ModOption>> records = new ArrayList<ETableRecord<ModOption>>();
        // add installed mods (read from database) to list
        List<Mod> installedMods = this.cfg.getDb().all(Mod.class);
        Map<String, ModOption> name_mod = new java.util.HashMap<String, ModOption>();
        for (Mod installedMod : installedMods) {
            ModOption mo = new ModOption(this.cfg, installedMod);
            name_mod.put(mo.getName(), mo);
            records.add(new ETableRecordBean<ModOption>(orderedFields, mo));
        }
        for (File modxml : modxmls) {
            ExceptionAndLogHandler.log(modxml);
            try {
                Mod availableMod = this.cfg.getXml().load(new FileInputStream(modxml), Mod.class);
                // mod not fit for mc version, do not show mod configuration
                if ((availableMod.getMcVersionDependency() != null) && !availableMod.getMcVersionDependency().contains("?")) { //$NON-NLS-1$
                    if (!this.match(availableMod.getMcVersionDependency(), this.cfg.getMcVersion())) {
                        continue;
                    }
                }
                // mod already added to list (read from database), do not show this configuration
                if (installedMods.contains(availableMod)) {
                    continue;
                }
                // add mod configuration
                ModOption otherModOption = name_mod.get(availableMod.getName());
                if (otherModOption == null) {
                    ModOption mo = new ModOption(this.cfg, availableMod);
                    name_mod.put(mo.getName(), mo);
                    records.add(new ETableRecordBean<ModOption>(orderedFields, mo));
                } else {
                    otherModOption.addMod(availableMod);
                }
            } catch (javax.xml.bind.UnmarshalException ex) {
                ExceptionAndLogHandler.log(ex);
                Throwable linkedException = ex.getLinkedException();
                StringBuilder sb = new StringBuilder();
                if (linkedException instanceof org.xml.sax.SAXParseException) {
                    sb.append("     lineNumber: ").append(org.xml.sax.SAXParseException.class.cast(linkedException).getLineNumber()).append("\n");//$NON-NLS-1$ //$NON-NLS-2$
                    sb.append("     columnNumber: ").append(org.xml.sax.SAXParseException.class.cast(linkedException).getColumnNumber()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append("     ").append(linkedException.getLocalizedMessage()); //$NON-NLS-1$
                UtilityMethods.showWarning(this.cfg, Messages.getString("ModOptionsWindow.xml_corrupt_title"), //$NON-NLS-1$
                        String.format(Messages.getString("ModOptionsWindow.xml_corrupt_message"), "     " + modxml.getName(), sb)); //$NON-NLS-1$ //$NON-NLS-2$
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

    private boolean match(String mcVersionDependency, String mcVersion) {
        String v1 = mcVersionDependency.substring(0, 3);
        String v2 = mcVersion.substring(0, 3);
        return v1.equals(v2);
    }
}
