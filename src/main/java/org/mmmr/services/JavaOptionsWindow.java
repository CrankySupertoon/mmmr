package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.mmmr.services.FancySwing.MoveMouseListener;
import org.mmmr.services.IOMethods.MemInfo;
import org.mmmr.services.swing.VerticalTableHeaderCellRenderer;

public class JavaOptionsWindow extends JFrame {
    private class CellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -9161606663652528876L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    this.setFont(JavaOptionsWindow.this.cfg.getFont18().deriveFont(10f));
	    if ("y".equals(value)) {
		this.setBackground(JavaOptionsWindow.this.green);
		this.setForeground(Color.white);
	    }
	    if ("n".equals(value)) {
		this.setBackground(JavaOptionsWindow.this.red);
		this.setForeground(Color.white);
	    }

	    return renderer;
	}
    }

    private static final long serialVersionUID = -2617077870487045855L;

    private Config cfg;

    private Vector<String> columnNames = new Vector<String>();

    private Vector<Vector<Object>> data = new Vector<Vector<Object>>();

    private Color green = Color.GREEN.darker();

    private String[] options = ("-Xms{MIN}m -Xmx{MAX}m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc")
	    .split(" ");

    private Color red = Color.RED.darker();

    private JTable table;

    public JavaOptionsWindow(final Config cfg) throws HeadlessException, IOException {
	this.cfg = cfg;
	this.setIconImage(cfg.getIcon().getImage());
	this.setTitle(cfg.getTitle());

	RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
	mainpanel.setShady(false);
	new MoveMouseListener(mainpanel);
	mainpanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
	this.getContentPane().add(mainpanel);

	final JTable table = this.getOptions();
	mainpanel.add(table, BorderLayout.CENTER);
	mainpanel.add(table.getTableHeader(), BorderLayout.NORTH);

	JButton quit = new JButton("Select an option and click here.");
	quit.setFont(cfg.getFont18());
	quit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    if (table.getSelectedRow() != -1) {
			StringBuilder sb = new StringBuilder();
			String jre = String.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0));
			sb.append("\"").append(jre).append("\\bin\\java.exe\"");
			String min = String.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 2));
			sb.append(" ").append(JavaOptionsWindow.this.options[0].replaceAll("\\Q{MIN}\\E", min));
			String max = String.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 3));
			sb.append(" ").append(JavaOptionsWindow.this.options[1].replaceAll("\\Q{MAX}\\E", max));
			for (int i = 4; i < JavaOptionsWindow.this.options.length + 1; i++) {
			    boolean optionAvailable = "y".equals(String.valueOf(table.getModel().getValueAt(table.getSelectedRow(), i)));
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
	this.setSize(800, this.getHeight());
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

	String[] names = { "JRE", "64bit", "Xms", "Xmx", "client", "UseConcMarkSweepGC", "DisableExplicitGC", "UseAdaptiveGCBoundary", "MaxGCPauseMillis", "UseGCOverheadLimit",
		"SurvivorRatio", "Xnoclassgc", "UseSSE", "Xincgc" };
	this.columnNames.addAll(Arrays.asList(names));

	for (String[] jreinfo : IOMethods.getAllJavaInfo(IOMethods.getAllJavaRuntimes())) {
	    String jre = jreinfo[0];
	    boolean _64 = "true".equals(jreinfo[2]);

	    int _min = min;
	    int _max = max;

	    if (!_64) {
		_min = Math.min(min, 1024);
		_max = Math.min(max, 1024);
	    }

	    String[] options = ("-Xms" + _min + "m -Xmx" + _max + "m -client -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:-UseGCOverheadLimit -XX:SurvivorRatio=12 -Xnoclassgc -XX:UseSSE=3 -Xincgc")
		    .split(" ");

	    Vector<Object> row = new Vector<Object>();
	    row.add(jre);
	    row.add(_64 ? "y" : "n");
	    for (int i = 0; i < options.length; i++) {
		String option = options[i];
		boolean result = IOMethods.process(true, false, jre + "/bin/java.exe", option, "-version").get(0).toLowerCase().startsWith("java version");
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
	    this.data.add(row);
	}

	DefaultTableModel model = new DefaultTableModel(this.data, this.columnNames) {
	    private static final long serialVersionUID = 7182945794160936753L;

	    @Override
	    public boolean isCellEditable(int rowIndex, int colIndex) {
		return false; // Disallow the editing of any cell
	    }
	};
	this.table = new JTable(model);

	TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
	Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
	while (columns.hasMoreElements()) {
	    TableColumn tc = columns.nextElement();
	    tc.setHeaderRenderer(headerRenderer);
	    tc.setCellRenderer(new CellRenderer());
	}

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
