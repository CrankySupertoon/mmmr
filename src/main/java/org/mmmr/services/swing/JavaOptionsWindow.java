package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.MMMR;
import org.mmmr.services.Messages;
import org.mmmr.services.UtilityMethods;
import org.mmmr.services.UtilityMethods.MemInfo;
import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecordCollection;
import org.mmmr.services.swing.common.RoundedPanel;
import org.mmmr.services.swing.common.UIUtils;
import org.mmmr.services.swing.common.UIUtils.MoveMouseListener;

/**
 * @author Jurgen
 */
public class JavaOptionsWindow extends JFrame {
    private static final long serialVersionUID = -2617077870487045855L;

    public static void main(String[] args) {
        try {
            UIUtils.lookAndFeel();
            Config cfg = new Config(args);
            new JavaOptionsWindow(cfg, null).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Config cfg;

    private String[] options = ("-Xms{MIN}m -Xmx{MAX}m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc -XX:+UseCompressedOops") //$NON-NLS-1$
            .split(" "); //$NON-NLS-1$

    private ETable table;

    public JavaOptionsWindow(final Config cfg, Dimension prefSize) throws HeadlessException, IOException {
        //Color selectionColor = Color.class.cast(UIManager.get("Table.selectionBackground")); //$NON-NLS-1$
        this.cfg = cfg;

        RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());

        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label, BorderLayout.NORTH);

        final ETable jtable = this.getOptions();
        jtable.setFont(cfg.getFontSmall());

        mainpanel.add(new JScrollPane(jtable), BorderLayout.CENTER);

        JButton choose = new JButton(Messages.getString("JavaOptionsWindow.select_and_click")); //$NON-NLS-1$
        choose.setFont(cfg.getFontLarge());
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (jtable.getSelectedRow() != -1) {
                        StringBuilder sb = new StringBuilder();
                        String jre = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 0));
                        sb.append("\"").append(jre).append("\\bin\\java.exe\""); //$NON-NLS-1$ //$NON-NLS-2$
                        String min = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 2));
                        sb.append(" ").append(JavaOptionsWindow.this.options[0].replaceAll("\\Q{MIN}\\E", min)); //$NON-NLS-1$ //$NON-NLS-2$
                        String max = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 3));
                        sb.append(" ").append(JavaOptionsWindow.this.options[1].replaceAll("\\Q{MAX}\\E", max)); //$NON-NLS-1$ //$NON-NLS-2$
                        for (int i = 4; i < (JavaOptionsWindow.this.options.length + 1); i++) {
                            boolean optionAvailable = "y".equals(String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), i))); //$NON-NLS-1$
                            if (optionAvailable) {
                                sb.append(" ").append(JavaOptionsWindow.this.options[i - 1]); //$NON-NLS-1$
                            }
                        }
                        sb.append(" -jar \"" + new File(cfg.getClientFolder(), "minecraft.jar").getAbsolutePath() + "\""); //$NON-NLS-1$
                        cfg.setMcCommandline(sb.toString());
                        cfg.setProperty("jre", jre); //$NON-NLS-1$
                        MMMR.writeMCBat(cfg);
                    }
                    JavaOptionsWindow.this.dispose();
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        mainpanel.add(choose, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        UIUtils.translucent(this);

        if (prefSize == null) {
            this.setSize(800, 300);
        } else {
            this.setSize(prefSize);
        }

        UIUtils.rounded(this);
        this.setResizable(false);
    }

    private ETable getOptions() throws IOException {
        int min;
        int max;
        MemInfo info = UtilityMethods.getMemInfo();
        if (info.memfreemb < 512) {
            min = 256;
            max = 1024;
        } else if (info.memfreemb < 1024) {
            min = 512;
            max = 1536;
        } else if (info.memfreemb < 2560) {
            min = 1024;
            max = 2048;
        } else {
            min = 2048;
            max = 2048;
        }

        ETableConfig configuration = new ETableConfig(true);
        configuration.setVertical(true);
        this.table = new ETable(configuration);

        this.table.setRowHeight(28);

        ETableHeaders columnNames = new ETableHeaders();

        columnNames.add("      JRE      "); //$NON-NLS-1$
        columnNames.add("64bit"); //$NON-NLS-1$
        for (String option : this.options) {
            columnNames.add(option);
        }
        columnNames.add("V");
        columnNames.add("*");

        this.table.getEventSafe().setHeaders(columnNames);

        List<ETableRecordCollection> records = new ArrayList<ETableRecordCollection>();

        for (String[] jreinfo : UtilityMethods.getAllJavaInfo(UtilityMethods.getAllJavaRuntimes())) {
            String jre = jreinfo[0];
            boolean _64 = "true".equals(jreinfo[2]); //$NON-NLS-1$

            int _min = min;
            int _max = max;

            if (!_64) {
                _min = Math.min(min, 1024);
                _max = Math.min(max, 1024);
            }

            String[] opts = new String[this.options.length];
            System.arraycopy(this.options, 0, opts, 0, this.options.length);
            opts[0] = opts[0].replaceAll("\\Q{MIN}\\E", "" + _min); //$NON-NLS-1$ //$NON-NLS-2$
            opts[1] = opts[1].replaceAll("\\Q{MAX}\\E", "" + _max); //$NON-NLS-1$ //$NON-NLS-2$

            Vector<Object> row = new Vector<Object>();
            int success = 0;
            row.add(jre);
            if (_64) {
                success++;
            }
            row.add(_64 ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
            for (int i = 0; i < opts.length; i++) {
                String option = opts[i];
                boolean result = UtilityMethods.process(true, false, jre + "/bin/java.exe", option, "-version").get(0).toLowerCase() //$NON-NLS-1$ //$NON-NLS-2$
                        .startsWith("java version"); //$NON-NLS-1$
                if (result) {
                    success++;
                }
                if (i == 0) {
                    if (result) {
                        row.add("" + _min); //$NON-NLS-1$
                    } else {
                        row.add("1024"); //$NON-NLS-1$
                    }
                } else if (i == 1) {
                    if (result) {
                        row.add("" + _max); //$NON-NLS-1$
                    } else {
                        row.add("1024"); //$NON-NLS-1$
                    }
                } else {
                    row.add((result ? "y" : "n")); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            row.add(jreinfo[1]);
            row.add(success);

            records.add(new ETableRecordCollection(row));
        }

        Collections.sort(records, new Comparator<ETableRecordCollection>() {
            @Override
            public int compare(ETableRecordCollection o1, ETableRecordCollection o2) {
                int s1 = Integer.parseInt(String.valueOf(o1.get(o1.size() - 1)));
                int s2 = Integer.parseInt(String.valueOf(o2.get(o2.size() - 1)));

                if (s1 != s2) {
                    return s2 - s1;
                }

                int x1 = Integer.parseInt(String.valueOf(o1.get(3)));
                int x2 = Integer.parseInt(String.valueOf(o2.get(3)));

                if (x1 != x2) {
                    return x2 - x1;
                }

                boolean d1 = String.valueOf(o1.get(0)).toLowerCase().contains("jdk"); //$NON-NLS-1$
                boolean d2 = String.valueOf(o2.get(0)).toLowerCase().contains("jdk"); //$NON-NLS-1$

                if (d1 && !d2) {
                    return 1;
                }
                if (!d1 && d2) {
                    return -1;
                }

                String v1 = String.valueOf(o1.get(o1.size() - 2));
                String v2 = String.valueOf(o2.get(o2.size() - 2));

                return v2.compareTo(v1);
            }
        });

        for (ETableRecordCollection r : records) {
            this.table.getEventSafe().addRecord(r);
        }

        for (int i = 0; i < columnNames.getColumnCount(); i++) {
            this.table.packColumn(i, 3);
        }

        return this.table;
    }

    public void selectDefault() {
        try {
            String defaultJre = this.cfg.getProperty("jre"); //$NON-NLS-1$
            if (defaultJre != null) {
                for (int i = 0; i < this.table.getRowCount(); i++) {
                    String jreOption = String.valueOf(this.table.getValueAt(i, 0));
                    if (defaultJre.equals(jreOption)) {
                        this.table.setRowSelectionInterval(i, i);
                    }
                }
            }
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }
}
