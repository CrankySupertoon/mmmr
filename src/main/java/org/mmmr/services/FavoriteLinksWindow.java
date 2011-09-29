package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableRecordArray;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;

/**
 * @author Jurgen
 */
public class FavoriteLinksWindow extends JWindow {
    /** serialVersionUID */
    private static final long serialVersionUID = -2596040987353689775L;

    public FavoriteLinksWindow(Config cfg) throws IOException {
        super((Window) FancySwing.getCurrentFrame());

        final ETable table = new ETable() {
            private static final long serialVersionUID = -8250534232070637135L;

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(this.getColumnModel()) {
                    private static final long serialVersionUID = 4738636168685789178L;

                    @Override
                    public void processMouseMotionEvent(MouseEvent me) {
                        this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        super.processMouseMotionEvent(me);
                    }
                };
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component c = this.super_prepareRenderer(renderer, rowIndex, vColIndex);
                String url = String.valueOf(this.getRecordAtVisualRow(rowIndex).get(1));
                JLabel.class.cast(c).setToolTipText(url);
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return c;
            }
        };
        ETableHeaders headers = new ETableHeaders();
        headers.add(Messages.getString("FavoriteLinksWindow.0")); //$NON-NLS-1$
        table.getEventSafe().setHeaders(headers);
        File f = new File(cfg.getData(), "links"); //$NON-NLS-1$
        for (File ff : f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("url"); //$NON-NLS-1$
            }
        })) {
            FileInputStream in = new FileInputStream(ff);
            Properties p = new Properties();
            p.load(in);
            in.close();
            table.getEventSafe().addRecord(new ETableRecordArray(ff.getName().replaceAll("\\.url", "").replaceAll("\\.URL", ""), p.get("URL"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }

        this.setAlwaysOnTop(true);
        final JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBorder(BorderFactory.createLineBorder(Color.black));
        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        mainpanel.setLayout(new BorderLayout());
        mainpanel.add(table.getTableHeader(), BorderLayout.NORTH);
        new MoveMouseListener(table.getTableHeader());
        mainpanel.add(table, BorderLayout.CENTER);
        JButton close = new JButton("close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainpanel.removeAll();
                FavoriteLinksWindow.this.setVisible(false);
                FavoriteLinksWindow.this.dispose();
            }
        });
        mainpanel.add(close, BorderLayout.SOUTH);
        this.pack();
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {
                        int row = table.rowAtPoint(e.getPoint());
                        if (row == -1) {
                            return;
                        }
                        int col = table.columnAtPoint(e.getPoint());
                        if (col == -1) {
                            return;
                        }
                        if (col == 0) {
                            String url = String.valueOf(table.getRecordAtVisualRow(row).get(1));
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(URI.create(url));
                            } else {
                                // TODO
                            }
                        }
                    }
                } catch (Exception ex) {
                    ExceptionAndLogHandler.log(ex);
                }
            }
        });
    }
}
