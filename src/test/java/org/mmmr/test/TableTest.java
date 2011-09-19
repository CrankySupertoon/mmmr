package org.mmmr.test;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

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
            final ETable table = new ETable(new ETableConfig(true, false, false));
            final ETableI safetable = table.getEventSafe();
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            final Random r = new Random(256955466579946l);
            frame.setVisible(true);
            ETableHeaders headers = new ETableHeaders();
            headers.add("test 1", Integer.class, true);
            headers.add("test 2", String.class, true);
            headers.add("test 3", Boolean.class, true);
            safetable.setHeaders(headers);
            for (int i = 0; i < 100; i++) {
                int next = r.nextInt(1000);
                safetable.addRecord(new ETableRecordArray(new Object[] { next, String.valueOf(next), Boolean.TRUE }));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
