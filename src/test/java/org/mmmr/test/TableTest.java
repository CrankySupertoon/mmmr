package org.mmmr.test;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.mmmr.services.swing.common.ETable;
import org.mmmr.services.swing.common.ETableConfig;
import org.mmmr.services.swing.common.ETableHeaders;
import org.mmmr.services.swing.common.ETableI;
import org.mmmr.services.swing.common.ETableRecordArray;
import org.mmmr.services.swing.common.FancySwing;

public class TableTest {
    public static void main(String[] args) {
        try {
            FancySwing.lookAndFeel();
            ETableConfig configuration = new ETableConfig(true);
            final ETable table = new ETable(configuration);
            final ETableHeaders headers = new ETableHeaders();
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
                            if ("15".equals("" + table.getRecordAtVisualRow(row).get(0))) {
                                System.out.println("cellvalue 15");
                            }
                            if ("test 1".equals(table.getColumnValueAtVisualColumn(col))) {
                                System.out.println("col 1");
                            }
                        }

                        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                            System.out.println(i);
                            System.out.println(table.getCellRenderer(1, i));
                            System.out.println(table.getCellEditor(1, i));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            final ETableI safetable = table.getEventSafe();
            final JFrame frame = new JFrame();
            frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            final Random r = new Random(256955466579946l);
            headers.add("test 1", Integer.class, true);
            headers.add("test 2", String.class, true);
            headers.add("test 3", Boolean.class, true);
            headers.add("test 4", Date.class, true);
            headers.add("test 5", Double.class, true);
            safetable.setHeaders(headers);
            for (int i = 0; i < 100; i++) {
                int next = r.nextInt(1000);
                safetable.addRecord(new ETableRecordArray(new Object[] { next, String.valueOf(next), Boolean.TRUE, new Date(), r.nextDouble() }));
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setVisible(true);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
