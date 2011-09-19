package org.mmmr.test;

import java.awt.BorderLayout;
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
            ETableConfig configuration = new ETableConfig();
            final ETable table = new ETable(configuration);
            final ETableI safetable = table.getEventSafe();
            final JFrame frame = new JFrame();
            frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            final Random r = new Random(256955466579946l);
            ETableHeaders headers = new ETableHeaders();
            headers.add("test \n 1", Integer.class, true);
            headers.add("test \n 2", String.class, true);
            headers.add("test \n 3", Boolean.class, true);
            safetable.setHeaders(headers);
            for (int i = 0; i < 100; i++) {
                int next = r.nextInt(1000);
                safetable.addRecord(new ETableRecordArray(new Object[] { next, String.valueOf(next), Boolean.TRUE }));
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
