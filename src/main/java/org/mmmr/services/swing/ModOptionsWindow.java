package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

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
    private class DateRenderer extends DefaultRenderer {
        private static final long serialVersionUID = -8217402048878663776L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
                value = Config.DATE_FORMAT.format(Date.class.cast(value));
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class DefaultRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -6412182708542004171L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setFont(ModOptionsWindow.this.cfg.getFontNarrow().deriveFont(12.0f));

            return this;
        }
    }

    private class UrlAsStringRenderer extends DefaultRenderer {
        private static final long serialVersionUID = -8217402048878663776L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setForeground(Color.BLUE);

            return this;
        }
    }

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
        ETableConfig configuration = new ETableConfig(true, false, true, true, true, true, false, true);
        final ETable options = new ETable(configuration);
        final ETableI safetable = options.getEventSafe();
        final List<String> orderedFields = new ArrayList<String>();

        options.setRowHeight(28);

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

        TableColumnModel columnModel = options.getColumnModel();
        columnModel.getColumn(orderedFields.indexOf("url")).setCellRenderer(new UrlAsStringRenderer());
        columnModel.getColumn(orderedFields.indexOf("installationDate")).setCellRenderer(new DateRenderer());

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
                records.add(new ETableRecordBean(orderedFields, availablemod));
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }

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
