package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.mmmr.services.Config;
import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.IOMethods;
import org.mmmr.services.IOMethods.MemInfo;
import org.mmmr.services.MMMR;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;
import org.mmmr.services.swing.common.VerticalTableHeaderCellRenderer;

/**
 * @author Jurgen
 */
public class JavaOptionsWindow extends JFrame {
    private class CellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -9161606663652528876L;

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row, column);
            renderer.setToolTipText(value == null ? null : String.valueOf(value));
            if (JavaOptionsWindow.this.cfg != null) {
                this.setFont(JavaOptionsWindow.this.cfg.getFont18().deriveFont(10f));
            }
            if ("y".equals(value)) {
                if (isSelected) {
                    this.setBackground(JavaOptionsWindow.this.greenSelected);
                } else {
                    this.setBackground(JavaOptionsWindow.this.green);
                }
                this.setForeground(Color.white);
                this.setHorizontalTextPosition(SwingConstants.CENTER);
            } else if ("n".equals(value)) {
                if (isSelected) {
                    this.setBackground(JavaOptionsWindow.this.redSelected);
                } else {
                    this.setBackground(JavaOptionsWindow.this.red);
                }
                this.setForeground(Color.white);
                this.setHorizontalTextPosition(SwingConstants.CENTER);
            } else {
                this.setBackground(Color.white);
                this.setForeground(Color.black);
                this.setHorizontalTextPosition(SwingConstants.CENTER);
            }
            return renderer;
        }
    }

    @SuppressWarnings("rawtypes")
    public class ColumnHeaderToolTips extends MouseMotionAdapter {
        // Current column whose tooltip is being displayed.
        // This variable is used to minimize the calls to setToolTipText().
        TableColumn curCol;

        // Maps TableColumn objects to tooltips

        Map tips = new HashMap();

        @Override
        public void mouseMoved(MouseEvent evt) {
            TableColumn col = null;
            JTableHeader header = (JTableHeader) evt.getSource();
            @SuppressWarnings("hiding")
            JTable table = header.getTable();
            TableColumnModel colModel = table.getColumnModel();
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());

            // Return if not clicked on any column header
            if (vColIndex >= 0) {
                col = colModel.getColumn(vColIndex);
            }

            if (col != this.curCol) {
                header.setToolTipText((String) this.tips.get(col));
                this.curCol = col;
            }
        }

        // If tooltip is null, removes any tooltip text.
        @SuppressWarnings("unchecked")
        public void setToolTip(TableColumn col, String tooltip) {
            if (tooltip == null) {
                this.tips.remove(col);
            } else {
                this.tips.put(col, tooltip);
            }
        }
    }

    private static final long serialVersionUID = -2617077870487045855L;

    public static void main(String[] args) {
        try {
            FancySwing.lookAndFeel();
            new JavaOptionsWindow(null, new Dimension(600, 300)).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Config cfg;

    private Vector<String> columnNames = new Vector<String>();

    private Vector<Vector<Object>> data = new Vector<Vector<Object>>();

    private Color green;

    private Color greenSelected;

    private String[] options = ("-Xms{MIN}m -Xmx{MAX}m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc -XX:+UseCompressedOops")
            .split(" ");

    private Color red;

    private Color redSelected;

    private JTable table;

    public JavaOptionsWindow(final Config cfg, Dimension preferredSize) throws HeadlessException, IOException {
        Color selectionColor = Color.class.cast(UIManager.get("Table.selectionBackground"));
        this.red = Color.RED.darker();
        this.redSelected = selectionColor == null ? this.red : new Color(Math.max(this.red.getRed(), selectionColor.getRed()), Math.max(
                this.red.getGreen(), selectionColor.getGreen()), Math.max(this.red.getRed(), selectionColor.getBlue()));
        this.green = Color.GREEN.darker();
        this.greenSelected = selectionColor == null ? this.green : new Color(Math.min(this.green.getRed(), selectionColor.getRed()), Math.min(
                this.green.getGreen(), selectionColor.getGreen()), Math.min(this.green.getRed(), selectionColor.getBlue()));

        this.cfg = cfg;
        if (cfg != null) {
            this.setIconImage(cfg.getIcon().getImage());
            this.setTitle(cfg.getTitle());
        }

        RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        this.getContentPane().add(mainpanel);

        final JTable jtable = this.getOptions();
        jtable.setBorder(BorderFactory.createRaisedBevelBorder());
        jtable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        mainpanel.add(jtable, BorderLayout.CENTER);
        mainpanel.add(jtable.getTableHeader(), BorderLayout.NORTH);

        JButton quit = new JButton("Select an option and click here.");
        if (cfg != null) {
            quit.setFont(cfg.getFont18());
        }
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if ((cfg != null) && (jtable.getSelectedRow() != -1)) {
                        StringBuilder sb = new StringBuilder();
                        String jre = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 0));
                        sb.append("\"").append(jre).append("\\bin\\java.exe\"");
                        String min = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 2));
                        sb.append(" ").append(JavaOptionsWindow.this.options[0].replaceAll("\\Q{MIN}\\E", min));
                        String max = String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), 3));
                        sb.append(" ").append(JavaOptionsWindow.this.options[1].replaceAll("\\Q{MAX}\\E", max));
                        for (int i = 4; i < JavaOptionsWindow.this.options.length + 1; i++) {
                            boolean optionAvailable = "y".equals(String.valueOf(jtable.getModel().getValueAt(jtable.getSelectedRow(), i)));
                            if (optionAvailable) {
                                sb.append(" ").append(JavaOptionsWindow.this.options[i - 1]);
                            }
                        }
                        sb.append(" -jar minecraft.jar");
                        cfg.setMcCommandline(sb.toString());
                        cfg.setProperty("jre", jre);
                        MMMR.writeMCBat(cfg);
                    }
                    JavaOptionsWindow.this.dispose();
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
        mainpanel.add(quit, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        FancySwing.translucent(this);
        this.pack();
        if (preferredSize != null) {
            this.setSize(preferredSize);
        }
        FancySwing.rounded(this);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    private JTable getOptions() throws IOException {
        int min;
        int max;
        MemInfo info = IOMethods.getMemInfo();
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

        this.columnNames.add("JRE");
        this.columnNames.add("64bit");
        this.columnNames.addAll(Arrays.asList(this.options));

        for (String[] jreinfo : IOMethods.getAllJavaInfo(IOMethods.getAllJavaRuntimes())) {
            String jre = jreinfo[0];
            boolean _64 = "true".equals(jreinfo[2]);

            int _min = min;
            int _max = max;

            if (!_64) {
                _min = Math.min(min, 1024);
                _max = Math.min(max, 1024);
            }

            String[] opts = new String[this.options.length];
            System.arraycopy(this.options, 0, opts, 0, this.options.length);
            opts[0] = opts[0].replaceAll("\\Q{MIN}\\E", "" + _min);
            opts[1] = opts[1].replaceAll("\\Q{MAX}\\E", "" + _max);

            Vector<Object> row = new Vector<Object>();
            int success = 0;
            row.add(jre);
            if (_64) {
                success++;
            }
            row.add(_64 ? "y" : "n");
            for (int i = 0; i < opts.length; i++) {
                String option = opts[i];
                boolean result = IOMethods.process(true, false, jre + "/bin/java.exe", option, "-version").get(0).toLowerCase()
                        .startsWith("java version");
                if (result) {
                    success++;
                }
                if (i == 0) {
                    if (result) {
                        row.add("" + _min);
                    } else {
                        row.add("1024");
                    }
                } else if (i == 1) {
                    if (result) {
                        row.add("" + _max);
                    } else {
                        row.add("1024");
                    }
                } else {
                    row.add((result ? "y" : "n"));
                }
            }
            row.add(jreinfo[1]);
            row.add(success);
            this.data.add(row);
        }

        Collections.sort(this.data, new Comparator<Vector<Object>>() {

            @Override
            public int compare(Vector<Object> o1, Vector<Object> o2) {
                int s1 = Integer.parseInt(String.valueOf(o1.lastElement()));
                int s2 = Integer.parseInt(String.valueOf(o2.lastElement()));

                if (s1 != s2) {
                    return s2 - s1;
                }

                int x1 = Integer.parseInt(String.valueOf(o1.get(3)));
                int x2 = Integer.parseInt(String.valueOf(o2.get(3)));

                if (x1 != x2) {
                    return x2 - x1;
                }

                boolean d1 = String.valueOf(o1.get(0)).toLowerCase().contains("jdk");
                boolean d2 = String.valueOf(o2.get(0)).toLowerCase().contains("jdk");

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

        DefaultTableModel model = new DefaultTableModel(this.data, this.columnNames) {
            private static final long serialVersionUID = 7182945794160936753L;

            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; // Disallow the editing of any cell
            }
        };
        this.table = new JTable(model);
        this.table.setRowSorter(new TableRowSorter<TableModel>(model));

        // tooltips on table header
        ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
        // vertical table header labels
        VerticalTableHeaderCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
        Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
        int index = 0;
        // custom table cell renderer;
        CellRenderer cellRenderer = new CellRenderer();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            // vertical table header labels
            tc.setHeaderRenderer(headerRenderer);
            // custom table cell renderer;
            tc.setCellRenderer(cellRenderer);
            // tooltips on table header
            tips.setToolTip(tc, this.columnNames.get(index++));
        }
        JTableHeader tableHeader = this.table.getTableHeader();

        // tooltips on table header
        tableHeader.addMouseMotionListener(tips);
        // can drag and drop columns
        tableHeader.setReorderingAllowed(false);
        // can resize columns?
        tableHeader.setResizingAllowed(false);

        return this.table;
    }

    public void packColumns() {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) this.table.getColumnModel();
        // this.setColWidth(colModel, 0, 300);
        this.setColWidth(colModel, 1, 20);
        this.setColWidth(colModel, 2, 40);
        this.setColWidth(colModel, 3, 40);
        for (int i = 4; i < this.columnNames.size(); i++) {
            this.setColWidth(colModel, i, 20);
        }
    }

    public void selectDefault() {
        try {
            String defaultJre = this.cfg.getProperty("jre");
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

    private void setColWidth(DefaultTableColumnModel colModel, int i, int w) {
        TableColumn col = colModel.getColumn(i);
        col.setPreferredWidth(w);
        col.setMaxWidth(w);
        col.setMinWidth(w);
        col.setWidth(w);
    }
}
